package com.seungma.daglo.presenter.list.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.seungma.daglo.data.datasource.character.remote.CharacterRemoteDataSourceImpl
import com.seungma.daglo.data.repository.CharacterDataRepositoryImpl
import com.seungma.daglo.databinding.FragmentCharacterListBinding
import com.seungma.daglo.domain.list.usecase.LoadCharactersUseCase
import com.seungma.daglo.network.retrofit.RetrofitClient
import com.seungma.daglo.presenter.list.CharactersListAdapter
import com.seungma.daglo.presenter.list.form.CharactersLoadForm
import com.seungma.daglo.presenter.list.viewmodel.CharacterViewModel
import kotlinx.coroutines.launch

class CharacterListFragment : Fragment() {
    private var _binding: FragmentCharacterListBinding? = null
    private val binding get() = _binding!!
    private var _adapter: CharactersListAdapter? = null
    private val adapter get() = _adapter!!

    private val characterViewModel: CharacterViewModel by lazy {
        // dataSource
        val characterDataSourceImpl = CharacterRemoteDataSourceImpl(
            retrofitClient = RetrofitClient
        )
        // repository
        val characterDataRepositoryImpl = CharacterDataRepositoryImpl(
            characterDatasource = characterDataSourceImpl
        )
        // useCase
        val loadCharactersUseCase =
            LoadCharactersUseCase(characterDataRepository = characterDataRepositoryImpl)
        // factory
        val factory = CharacterViewModelFactory(
            loadCharactersUseCase = loadCharactersUseCase
        )

        ViewModelProvider(this, factory).get(CharacterViewModel::class.java)
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

        viewLifecycleOwner.lifecycleScope.launch {
            characterViewModel.loadCharacters(charactersLoadForm = CharactersLoadForm(reload = true))
            subscribe()
        }


        _adapter = CharactersListAdapter(
            itemClick = {

            }
        )

        binding.apply {
            rvCharacterList.adapter = adapter

            swipeRefreshLayout.setOnRefreshListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        characterViewModel.loadCharacters(
                            charactersLoadForm = CharactersLoadForm(
                                reload = true
                            )
                        )
                    } catch (e: Exception) {

                    } finally {
                        swipeRefreshLayout.isRefreshing = false
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun subscribe() {
        characterViewModel.viewState.collect {
            Log.d("seungma", "콜렉트" + it.characters)
            adapter.submitList(it.characters)
        }
    }

}

class CharacterViewModelFactory(
    private val loadCharactersUseCase: LoadCharactersUseCase

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CharacterViewModel(
            loadCharactersUseCase = loadCharactersUseCase
        ) as T
    }
}