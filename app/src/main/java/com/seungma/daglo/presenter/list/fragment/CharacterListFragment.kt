package com.seungma.daglo.presenter.list.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seungma.daglo.databinding.FragmentCharacterListBinding
import com.seungma.daglo.di.components.DaggerCharacterListFragmentComponent
import com.seungma.daglo.domain.list.entity.CharacterItemKeyEntity
import com.seungma.daglo.presenter.EndPoint
import com.seungma.daglo.presenter.Navigable
import com.seungma.daglo.presenter.list.CharactersListAdapter
import com.seungma.daglo.presenter.list.form.CharactersLoadForm
import com.seungma.daglo.presenter.list.viewmodel.CharacterViewEvent
import com.seungma.daglo.presenter.list.viewmodel.CharacterViewModel
import com.seungma.daglo.presenter.list.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

class CharacterListFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val characterViewModel: CharacterViewModel by viewModels { viewModelFactory }

    private var _binding: FragmentCharacterListBinding? = null
    private val binding get() = _binding!!
    private var _adapter: CharactersListAdapter? = null
    private val adapter get() = _adapter!!


    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
            val totalItemCount = layoutManager.itemCount
            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

            val threshold = 3
            val isLoading = characterViewModel.viewState.value.isLoading
            if (!isLoading && lastVisibleItemPosition >= totalItemCount - threshold) {
                viewLifecycleOwner.lifecycleScope.launch {
                    characterViewModel.viewState.value.nextPage?.let { nextPage ->
                        if (!characterViewModel.viewState.value.isLoading) {
                            characterViewModel.loadMore(
                                charactersLoadForm = CharactersLoadForm(
                                    page = nextPage,
                                    keyword = binding.etText.text.toString()
                                )
                            )
                        }
                    } ?: run {
                        Toast.makeText(requireContext(), "마지막 페이지입니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerCharacterListFragmentComponent.create().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharacterListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _adapter = CharactersListAdapter(
            itemClick = {
                val endPoint = EndPoint.CharacterInfo(
                    characterItemKeyEntity = CharacterItemKeyEntity(
                        characterId = it.id
                    )
                )
                (requireActivity() as? Navigable)?.navigateFragment(endPoint)
            }
        )

        binding.apply {
            rvCharacterList.adapter = adapter
            rvCharacterList.addOnScrollListener(onScrollListener)

            subscribe()
            swipeRefreshLayout.setOnRefreshListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    if (!characterViewModel.viewState.value.isLoading) {
                        characterViewModel.fetch(query = binding.etText.text.toString())
                        swipeRefreshLayout.isRefreshing = false
                    }
                }
            }

            etText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // 텍스트 변경 전
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // 텍스트 변경 중
                }

                override fun afterTextChanged(s: Editable?) {
                    characterViewModel.sendQuery(query = s?.toString() ?: "")
                }
            })

            etText.setText(characterViewModel.viewState.value.keyword)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvCharacterList.removeOnScrollListener(onScrollListener)
        binding.rvCharacterList.adapter = null
        _adapter = null
        _binding = null
    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                characterViewModel.viewState.filter { !it.isLoading }.collect {
                    adapter.submitList(it.characters) {
                        if (it.isFirstPage) {
                            binding.rvCharacterList.scrollToPosition(0)
                        }
                    }
                }
            }

            launch {
                characterViewModel.viewEvent.collect {

                    when (it) {
                        is CharacterViewEvent.Error -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }

                        else -> {}
                    }

                }
            }

        }
    }

}