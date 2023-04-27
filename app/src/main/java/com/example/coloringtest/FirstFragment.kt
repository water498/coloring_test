package com.example.coloringtest

import android.content.res.ColorStateList
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.coloringtest.databinding.FragmentFirstBinding
import com.example.coloringtest.util.BitmapUtils
import com.example.coloringtest.util.ColoringView
import com.example.coloringtest.util.DialogUtils
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

private var _binding: FragmentFirstBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
      _binding = FragmentFirstBinding.inflate(inflater, container, false)


      return binding.root

    }

    private fun setBinding(){

        binding.coloringView.apply {
            onColoringViewTouchListener = object : ColoringView.OnColoringViewTouchListener{
                override fun onActionDown() {

                }

                override fun onActionMove() {
                    binding.back.visibility = View.GONE
                    binding.redoUndoLinear.visibility = View.GONE
                    binding.selectColor.visibility = View.GONE
                    binding.menu.visibility = View.GONE
                }

                override fun onActionUp() {
                    binding.back.visibility = View.VISIBLE
                    binding.redoUndoLinear.visibility = View.VISIBLE
                    binding.selectColor.visibility = View.VISIBLE
                    binding.menu.visibility = View.VISIBLE
                }

                override fun onActionPointerDown() {

                }
            }
        }

        binding.undo.setOnClickListener { binding.coloringView.undo() }
        binding.redo.setOnClickListener { binding.coloringView.redo() }

        binding.selectColor.setOnClickListener {
            ColorPickerDialog
                .Builder(requireContext())        				// Pass Activity Instance
                .setTitle("Pick Theme")           	// Default "Choose Color"
                .setColorShape(ColorShape.SQAURE)   // Default ColorShape.CIRCLE
                .setDefaultColor(R.color.teal_200)     // Pass Default Color
                .setColorListener { color, colorHex ->
                    // Handle Color Selection
                    binding.coloringView.setColor(color)
                    binding.selectColor.backgroundTintList = ColorStateList.valueOf(color)
                    binding.coloringView.setShader(null)
                    Log.d("tag", "color : $color, colorHex : $colorHex")
                }
                .show()
        }

        binding.loadImage.setOnClickListener {
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.image2) // 비트맵으로 변환
            val resizeBitmap = BitmapUtils.resizeBitmap(bitmap, 1000, 1000)
            binding.coloringView.setImageBitmap(resizeBitmap) // ColoringView에 이미지 설정
        }

        binding.next.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }


        binding.thickness.setOnClickListener { DialogUtils.showNumberInputDialog(
            context = requireContext(), // Context 객체
            title = "숫자 입력", // 다이얼로그 제목
            message = "숫자를 입력하세요.", // 다이얼로그 메시지
            positiveButtonText = "확인", // 확인 버튼 텍스트
            negativeButtonText = "취소", // 취소 버튼 텍스트
            listener = object : DialogUtils.NumberInputDialogListener {
                override fun onNumberInputPositive(number: Int) {
                    binding.coloringView.setStrokeWidth(number.toFloat())
                }
                override fun onNumberInputError(errorMessage: String) {}
                override fun onNumberInputNegative() {}
            }
        ) }

        binding.brushType.setOnClickListener {
//            val colors = intArrayOf(Color.RED, Color.GREEN, Color.BLUE)
//            val positions = floatArrayOf(0f, 0.5f, 1f)
//            val linearGradient = LinearGradient(0f, 0f, 500f, 0f, colors, positions, Shader.TileMode.REPEAT)
//            binding.coloringView.setShader(linearGradient)

            val bitmap = BitmapFactory.decodeResource(requireContext().resources, R.drawable.ic_star2)
            val newBitmap = BitmapUtils.resizeBitmap(bitmap, 50, 50)
            val shader = BitmapShader(newBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
            binding.coloringView.setShader(shader)
        }

        binding.brushGradient?.setOnClickListener {

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBinding()

    }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}