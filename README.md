# APTDemo 
设计基于APT的注解处理器，参考ButterKnife，利用编译过程注解解析流程，读取@BindView 注解，生成实现了findViewById的java类，最后在Activity中使用。
1. 声明注解，创建apt-annotation的java模块
2. 创建注解处理器，创建apt-processor的java模块，引入注解的包和生成Java文件的javapoet包
3. 创建Android子模块apt-library，利用反射调用javapoet创建的java文件执行findViewById操作
# ASMDemo
实现了在方法定义的前添加代码，实现了在调用某方法的时做方法地址Hook替换成另外的方法引用（注意：Hook方法的签名只能处理当前类的方法，不能处理基类或者子类的方法，因为签名不同无法识别，比如HooK Activity的getIntent方法，如果调用的地方是执行子类Activity的getIntent,则无法识别，必须指定子类Activity.getIntent也在class to Dex过程中被查找替换）
# RooMDemo
参考Google官方案例实现的Room+FLow+LiveData+ViewModel,架构上定义了repository。
