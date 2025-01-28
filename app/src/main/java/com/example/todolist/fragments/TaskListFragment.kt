package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.databinding.FragmentTaskListBinding

class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!
    private val taskViewModel: TaskViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskAdapter = TaskAdapter(
            onEditClick = { task ->
                // Otwórz fragment edycji zadania
                AddEditTaskFragment.newInstance(task.id).show(parentFragmentManager, "EDIT_TASK")
            },
            onDeleteClick = { task ->
                // Usuń zadanie
                taskViewModel.deleteTask(task)
            }
        )

        binding.taskRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
        }

        taskViewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            taskAdapter.submitList(tasks)
        }

        binding.addTaskButton.setOnClickListener {
            AddEditTaskFragment.newInstance(null).show(parentFragmentManager, "ADD_TASK")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}