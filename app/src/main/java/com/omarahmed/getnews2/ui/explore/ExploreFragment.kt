package com.omarahmed.getnews2.ui.explore

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.omarahmed.getnews2.R
import com.omarahmed.getnews2.databinding.FragmentExploreBinding
import com.omarahmed.getnews2.util.Resource
import com.omarahmed.getnews2.util.showShareBottomSheet
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ExploreFragment : Fragment(R.layout.fragment_explore), CategoriesAdapter.OnUpdatePositionListener {

    private val exploreViewModel: ExploreViewModel by viewModels()
    private lateinit var exploreAdapter: ExploreAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentExploreBinding.bind(view)

        exploreAdapter = ExploreAdapter(
            onShareClick = {showShareBottomSheet(it.url)},
            onBookmarked = {exploreNews ->
                exploreViewModel.onBookmarkedClick(exploreNews)
            }
        )
        binding.apply {
            ivSearch.setOnClickListener {
                findNavController().navigate(R.id.action_global_searchFragment)
            }
            exploreViewModel.position.observe(viewLifecycleOwner) { position ->
                val categoriesAdapter = CategoriesAdapter(position,exploreViewModel.categories,this@ExploreFragment)
                gvCategories.adapter = categoriesAdapter
                val category = exploreViewModel.onGetCategory(position)
                exploreViewModel.getExploreNews(category).observe(viewLifecycleOwner){result ->
                    exploreAdapter.submitList(result.data)

                    pbExplore.isVisible = result is Resource.Loading
                    ivConnectionError.isVisible = result is Resource.Error
                    tvInternetError.isVisible = result is Resource.Error
                    tvInternetError.text = getString(R.string.check_internet_connection)
                }
            }
            rvExplore.apply {
                adapter = exploreAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
    }

    override fun onUpdatePosition(position: Int) {
        exploreViewModel.onUpdatePosition(position)
    }

}
