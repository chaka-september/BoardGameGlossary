package com.chakaseptember.boardgameglossary.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chakaseptember.boardgameglossary.R
import com.chakaseptember.boardgameglossary.database.Word
import com.chakaseptember.boardgameglossary.databinding.FragmentGlossaryBinding
import com.chakaseptember.boardgameglossary.databinding.ItemGlossaryBinding
import kotlinx.android.synthetic.main.fragment_glossary.*

class GlossaryFragment : Fragment() {

    private val viewModel: GlossaryViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(
            this,
            GlossaryViewModel.Factory(activity.application)
        ).get(GlossaryViewModel::class.java)
    }

    private lateinit var viewModelAdapter: GlossaryAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.words.observe(viewLifecycleOwner, Observer<List<Word>> { words ->
            words?.apply {
                viewModelAdapter?.words = words
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.searchWords(newText).observe(this@GlossaryFragment, Observer { words ->
                    words?.apply {
                        viewModelAdapter?.words = words
                    }
                })
                glossaryRecyclerView.scrollToPosition(0)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.searchWords(query).observe(this@GlossaryFragment, Observer { words ->
                    words?.apply {
                        viewModelAdapter?.words = words
                    }
                })
                glossaryRecyclerView.scrollToPosition(0)
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentGlossaryBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_glossary,
            container,
            false
        )
        // Set the lifecycleOwner so DataBinding can observe LiveData
        binding.setLifecycleOwner(viewLifecycleOwner)

        binding.glossaryViewModel = viewModel

        setHasOptionsMenu(true)

        viewModelAdapter = GlossaryAdapter()

        binding.root.findViewById<RecyclerView>(R.id.glossaryRecyclerView).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewModelAdapter
        }


        // Observer for the network error.
        viewModel.eventNetworkError.observe(this, Observer<Boolean> { isNetworkError ->
            if (isNetworkError) onNetworkError()
        })

        return binding.root
    }

    /**
     * Method for displaying a Toast error message for network errors.
     */
    private fun onNetworkError() {
        if (!viewModel.isNetworkErrorShown.value!!) {
            Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
            viewModel.onNetworkErrorShown()
        }
    }
}


/**
 * RecyclerView Adapter for setting up data binding on the items in the list.
 */
class GlossaryAdapter : RecyclerView.Adapter<GlossaryViewHolder>() {

    /**
     * The videos that our Adapter will show
     */
    var words: List<Word> = emptyList()
        set(value) {
            field = value
            // For an extra challenge, update this to use the paging library.

            // Notify any registered observers that the data set has changed. This will cause every
            // element in our RecyclerView to be invalidated.
            notifyDataSetChanged()
        }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GlossaryViewHolder {
        val withDataBinding: ItemGlossaryBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            GlossaryViewHolder.LAYOUT,
            parent,
            false
        )
        return GlossaryViewHolder(withDataBinding)
    }

    override fun getItemCount() = words.size

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     */
    override fun onBindViewHolder(holder: GlossaryViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.word = words[position]
        }
    }

}

/**
 * ViewHolder for Glossary items. All work is done by data binding.
 */
class GlossaryViewHolder(val viewDataBinding: ItemGlossaryBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.item_glossary
    }
}
