package com.example.todolist

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.todolist.databinding.FragmentAddEditTaskBinding

class AddEditTaskFragment : DialogFragment() {

    private var _binding: FragmentAddEditTaskBinding? = null
    private val binding get() = _binding!!
    private val taskViewModel: TaskViewModel by viewModels()
    private var taskId: Int? = null

    companion object {
        fun newInstance(taskId: Int?): AddEditTaskFragment {
            val fragment = AddEditTaskFragment()
            val args = Bundle()
            args.putInt("TASK_ID", taskId ?: -1)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskId = arguments?.getInt("TASK_ID", -1).takeIf { it != -1 }

        if (taskId != null) {
            taskViewModel.getTaskById(taskId!!).observe(viewLifecycleOwner) { task ->
                task?.let {
                    binding.taskTitleInput.setText(it.title)
                    binding.taskDescriptionInput.setText(it.description)
                }
            }
        }

        binding.saveButton.setOnClickListener {
            val title = binding.taskTitleInput.text.toString().trim()
            val description = binding.taskDescriptionInput.text.toString().trim()

            if (title.isNotEmpty()) {
                if (taskId == null) {
                    taskViewModel.insertTask(Task(0, title, description))
                } else {
                    taskViewModel.updateTask(Task(taskId!!, title, description))
                }
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
