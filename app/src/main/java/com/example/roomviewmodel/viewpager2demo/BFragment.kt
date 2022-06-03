package com.example.roomviewmodel.viewpager2demo

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.roomviewmodel.R

class BFragment : Fragment() {

    companion object {
        fun newInstance() = BFragment()
    }

    private lateinit var viewModel: BViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.b_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(BViewModel::class.java)
        // TODO: Use the ViewModel
    }

}