package com.example.coloringtest.view.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.MainThread
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator


abstract class BaseFragment<T: ViewDataBinding>(@LayoutRes private val layoutId:Int) : Fragment() {
    protected val TAG = this::class.java.simpleName

    private var _binding: T? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        Log.d(TAG,"BaseFragment onCreateView")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG,"BaseFragment onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
    }


    override fun onStart() {
        Log.d(TAG,"BaseFragment onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.d(TAG,"BaseFragment onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG,"BaseFragment onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG,"BaseFragment onStop")
        super.onStop()
    }

    override fun onDestroyView() {
        Log.d(TAG,"BaseFragment onDestroyView")
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        Log.d(TAG,"BaseFragment onDestroy")
        super.onDestroy()
    }






    // 연속 navigate 시 충돌 방지
    fun NavController.navigateSafe(
        @IdRes resId: Int,
        args: Bundle? = null,
        navOptions: NavOptions? = null,
        navExtras: Navigator.Extras? = null
    ) {
        val action = currentDestination?.getAction(resId) ?: graph.getAction(resId)

        if (action != null && currentDestination?.id != action.destinationId) {
            navigate(resId, args, navOptions, navExtras)
        }
    }

    @MainThread
    fun NavController.navigateSafe(directions: NavDirections) {
        navigateSafe(directions.actionId, directions.arguments, null)
    }

}