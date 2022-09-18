package ru.myitschool.lab23.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.myitschool.lab23.R
import ru.myitschool.lab23.databinding.FragmentGeneratedListBinding


// https://github.com/NorbertRudzki/pomocny_sasiad/blob/master/app/src/main/java/com/example/pomocnysasiad/view/OpinionAdapter.kt

class GeneratedListFragment : Fragment() {

    private val gviewModel: MainViewModel by sharedViewModel<MainViewModel>()
    private lateinit var binding: FragmentGeneratedListBinding

    /*companion object {
        fun newInstance() = GeneratedListFragment()
    }*/

    // private var _binding: FragmentGeneratedListBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    // private val binding get() = _binding!!

    // private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // _binding = FragmentGeneratedListBinding.inflate(inflater, container, false)
        binding = FragmentGeneratedListBinding.inflate(inflater, container, false)
        return binding.root
        // return inflater.inflate(R.layout.fragment_generated_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.message.text = "New text here!"



        gviewModel.getGeneratedListData().observe(viewLifecycleOwner) {
            requests ->
            if (!requests.isNullOrEmpty()){
                // add recyclerview.adapter
                binding.generatedList.layoutManager = LinearLayoutManager(context)
                val generatedListAdapter = GeneratedListAdapter(requests)
                binding.generatedList.adapter = generatedListAdapter
                Log.d("Tests", "${requests[1]}")
                // Log.d("Tests", "${binding.generatedList.adapter != null}")

            } else {
                Log.d("Tests", "empty requests")
            }
        }

    }
}

class GeneratedListAdapter(private val list: List<Double>) :
    RecyclerView.Adapter<GeneratedListAdapter.GeneratedListViewHolder>() {
    inner class GeneratedListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // TODO add layout
        val user: TextView = view.findViewById(R.id.random_number_result)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeneratedListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        // TODO add layout
        val row = layoutInflater.inflate(R.layout.item_layout, parent, false)
        return GeneratedListViewHolder(row)
    }

    override fun onBindViewHolder(holder: GeneratedListViewHolder, position: Int) {
        holder.user.text = list[position].toString()
    }

    override fun getItemCount() = list.size
}