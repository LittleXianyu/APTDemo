package com.example.roomviewmodel

import com.android.annotations.NonNull
import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import com.example.roomviewmodel.ClassVisitorFactory

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES

class CusPlugin extends Transform implements Plugin<Project>{

    @Override
    void apply(Project project) {
        println("this is CusPlugin")
        //registerTransform
        def android = project.extensions.getByType(AppExtension)
        android.registerTransform(this)
    }

    @Override
    String getName() {
        return "CusPlugin"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(@NonNull TransformInvocation transformInvocation) {
        println '--------------- AsmPlugin visit start --------------- '
        Collection<TransformInput> inputs = transformInvocation.inputs
        TransformOutputProvider outputProvider = transformInvocation.outputProvider
        //删除之前的输出
        if (outputProvider != null)
            outputProvider.deleteAll()
        //遍历inputs
        inputs.each { TransformInput input ->
            //遍历directoryInputs
            input.directoryInputs.each { DirectoryInput directoryInput ->
                handleDirectoryInput(directoryInput, outputProvider)
            }

            //遍历jarInputs
            input.jarInputs.each { JarInput jarInput ->
                handleJarInputs(jarInput, outputProvider)
            }
        }
        println '--------------- AsmPlugin visit end --------------- '
    }

    static void handleDirectoryInput(DirectoryInput directoryInput, TransformOutputProvider outputProvider) {
        //是否是目录
        if (directoryInput.file.isDirectory()) {
            //列出目录所有文件（包含子文件夹，子文件夹内文件）
            directoryInput.file.eachFileRecurse { File file ->
                def name = file.name
                if (ClassVisitorFactory.shouldVisitClass(name)) {
                    ClassReader classReader = new ClassReader(file.bytes)
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    ClassVisitor cv = ClassVisitorFactory.createClassVisitor(classWriter)
                    if (classReader != null && classWriter != null && cv != null) {
                        classReader.accept(cv, EXPAND_FRAMES)
                        byte[] code = classWriter.toByteArray()
                        FileOutputStream fos = new FileOutputStream(
                                file.parentFile.absolutePath + File.separator + name)
                        fos.write(code)
                        fos.close()
                    }
                }
            }
        }

        //处理完输入文件之后，要把输出给下一个任务
        def dest = outputProvider.getContentLocation(directoryInput.name,
                directoryInput.contentTypes, directoryInput.scopes,
                Format.DIRECTORY)
        FileUtils.copyDirectory(directoryInput.file, dest)
    }

    static void handleJarInputs(JarInput it, TransformOutputProvider outputProvider) {
        def outputFile = outputProvider.getContentLocation(
                it.name, it.contentTypes, it.scopes, Format.JAR)
        if (!it.file.name.endsWith(".jar")) {
            if (it.file.isDirectory()) {
                FileUtils.copyDirectory(it.file, outputFile)
            } else {
                FileUtils.copyFile(it.file, outputFile)
            }
            return
        }
        def parentFile = outputFile.parentFile
        if (parentFile.exists() || outputFile.exists()) {
            parentFile.delete()
        }
        if (parentFile.mkdirs()) {
            outputFile.createNewFile()
        }
        JarOutputStream jarOutputStream = new JarOutputStream(outputFile.newOutputStream())
        JarFile jarFile = new JarFile(it.file)
        //用于保存
        jarFile.entries().each { JarEntry jarEntry ->
            String entryName = jarEntry.name
            ZipEntry zipEntry = new ZipEntry(entryName)
            InputStream inputStream = jarFile.getInputStream(jarEntry)
            //插桩class
            if (ClassVisitorFactory.shouldVisitClass(entryName)) {
                //class文件处理
                jarOutputStream.putNextEntry(zipEntry)
                ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                ClassVisitor cv = ClassVisitorFactory.createClassVisitor(classWriter)
                if (classReader != null && classWriter != null && cv != null) {
                    classReader.accept(cv, EXPAND_FRAMES)
                    byte[] code = classWriter.toByteArray()
                    jarOutputStream.write(code)
                }
            } else {
                jarOutputStream.putNextEntry(zipEntry)
                jarOutputStream.write(IOUtils.toByteArray(inputStream))
            }
            jarOutputStream.closeEntry()
        }
        //结束
        jarOutputStream.close()
        jarFile.close()
    }
}
