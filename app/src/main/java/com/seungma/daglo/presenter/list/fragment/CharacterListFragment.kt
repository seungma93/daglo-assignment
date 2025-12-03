package com.seungma.daglo.presenter.list.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seungma.daglo.DagloApplication
import com.seungma.daglo.databinding.FragmentCharacterListBinding
import com.seungma.daglo.domain.list.entity.CharacterItemKeyEntity
import com.seungma.daglo.presenter.EndPoint
import com.seungma.daglo.presenter.Navigable
import com.seungma.daglo.presenter.list.CharactersListAdapter
import com.seungma.daglo.presenter.list.form.CharactersLoadForm
import com.seungma.daglo.presenter.list.form.CharactersSearchForm
import com.seungma.daglo.presenter.list.viewmodel.CharacterViewModel
import com.seungma.daglo.presenter.list.viewmodel.ViewModelFactory
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
    private var scrollPosition = 0

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            
            val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
            val totalItemCount = layoutManager.itemCount
            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            
            // 마지막 아이템에서 3개 전에 도달했을 때 다음 페이지 로드
            val threshold = 3
            val isLoading = characterViewModel.viewState.value.isLoading
            if (!isLoading && lastVisibleItemPosition >= totalItemCount - threshold) {
                viewLifecycleOwner.lifecycleScope.launch {
                    when(characterViewModel.viewState.value.isLast) {
                        true -> {}
                        false -> {

                            when(binding.etText.text.isNullOrBlank()) {
                                true -> {
                                    characterViewModel.loadCharacters(
                                        charactersLoadForm = CharactersLoadForm(reload = false)
                                    )
                                }
                                false -> {
                                    characterViewModel.searchCharacters(
                                        charactersSearchForm = CharactersSearchForm(keyword = binding.etText.text?.toString() ?: "", reload = false)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as DagloApplication)
            .appComponent
            .inject(this)
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

        // 스크롤 위치 복원
        savedInstanceState?.let {
            scrollPosition = it.getInt("scroll_position", 0)
        }

        _adapter = CharactersListAdapter(
            itemClick = {
                val endPoint = EndPoint.CharacterItem(
                    characterItemKeyEntity = CharacterItemKeyEntity(
                        characterEntity = it
                    )
                )
                (requireActivity() as? Navigable)?.navigateFragment(endPoint)
            }
        )

        binding.apply {
            rvCharacterList.adapter = adapter
            rvCharacterList.addOnScrollListener(onScrollListener)

            swipeRefreshLayout.setOnRefreshListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    try {

                        when(binding.etText.text.isNullOrBlank()) {
                            true -> {
                                characterViewModel.loadCharacters(
                                    charactersLoadForm = CharactersLoadForm(reload = true)
                                )
                            }
                            false -> {
                                characterViewModel.searchCharacters(
                                    charactersSearchForm = CharactersSearchForm(keyword = binding.etText.text?.toString() ?: "", reload = true)
                                )
                            }
                        }
                    } catch (e: Exception) {

                    } finally {
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


                    // 텍스트 변경 후
                    if (s.isNullOrBlank()) {
                        // 텍스트 X

                        characterViewModel.viewState.value.keyword?.let {
                            characterViewModel.clearViewState()
                        }

                        viewLifecycleOwner.lifecycleScope.launch {
                            if (characterViewModel.viewState.value.characters.isEmpty()) {
                                characterViewModel.loadCharacters(charactersLoadForm = CharactersLoadForm(reload = true))
                            }
                        }

                    } else {
                        // 텍스트 O
                        characterViewModel.viewState.value.keyword?.let {
                            if(it != s.toString()) {
                                characterViewModel.clearViewState()
                            }
                        } ?: run {
                            characterViewModel.clearViewState()
                        }

                        viewLifecycleOwner.lifecycleScope.launch {
                            if (characterViewModel.viewState.value.characters.isEmpty()) {
                                characterViewModel.searchCharacters(charactersSearchForm = CharactersSearchForm(
                                    keyword = s.toString(),
                                    reload = true
                                ))
                            }
                        }
                    }
                }
            })

        }

        // ViewModel에 데이터가 없을 때만 초기 로드
        viewLifecycleOwner.lifecycleScope.launch {
            if (characterViewModel.viewState.value.characters.isEmpty()) {
                characterViewModel.loadCharacters(charactersLoadForm = CharactersLoadForm(reload = true))
            }
            subscribe()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 스크롤 위치 저장
        binding.rvCharacterList.layoutManager?.let { layoutManager ->
            if (layoutManager is LinearLayoutManager) {
                val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                outState.putInt("scroll_position", firstVisiblePosition)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvCharacterList.removeOnScrollListener(onScrollListener)
        binding.rvCharacterList.adapter = null
        _adapter = null
        _binding = null
    }

    private suspend fun subscribe() {
        characterViewModel.viewState.collect {
            Log.d("seungma", "콜렉트" + it.characters)
            val layoutManager = binding.rvCharacterList.layoutManager as? LinearLayoutManager
            
            adapter.submitList(it.characters) {
                // 리스트 업데이트 후 스크롤 위치 복원
                if (scrollPosition > 0 && scrollPosition < it.characters.size) {
                    layoutManager?.scrollToPosition(scrollPosition)
                    scrollPosition = 0 // 복원 후 초기화
                }
            }
        }
    }

}