package com.seungma.daglo.presenter.list.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.seungma.daglo.databinding.FragmentCharacterBinding
import com.seungma.daglo.di.components.DaggerCharacterFragmentComponent
import com.seungma.daglo.domain.list.entity.CharacterItemKeyEntity
import com.seungma.daglo.presenter.list.form.CharacterLoadForm
import com.seungma.daglo.presenter.list.viewmodel.CharacterInfoViewEvent
import com.seungma.daglo.presenter.list.viewmodel.CharacterInfoViewModel
import com.seungma.daglo.presenter.list.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import javax.inject.Inject

class CharacterFragment : Fragment() {
    companion object {
        const val CHARACTER_ITEM_KEY = "CHARACTER_ITEM_KEY"

        fun newInstance(
            characterItemKeyEntity: CharacterItemKeyEntity
        ): CharacterFragment {
            return CharacterFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(CHARACTER_ITEM_KEY, characterItemKeyEntity)
                }
            }
        }
    }
    private val characterItemKeyEntity: CharacterItemKeyEntity?
        get() = arguments?.getParcelable(CHARACTER_ITEM_KEY, CharacterItemKeyEntity::class.java)


    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val characterInfoViewModel: CharacterInfoViewModel by viewModels { viewModelFactory }

    private var _binding: FragmentCharacterBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerCharacterFragmentComponent.create().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharacterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        characterItemKeyEntity?.let {
            characterInfoViewModel.loadCharacter(characterLoadForm = CharacterLoadForm(id = it.characterId))
        }

        subscribe()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                characterInfoViewModel.viewState.collect { value ->

                    binding.apply {
                        value.character.let {
                            Glide.with(requireContext())
                                .load(it.image)
                                .into(ivImage)

                            tvName.text = it.name
                            tvGender.text = it.gender
                            tvStatus.text = it.status
                            tvSpecies.text = it.species
                            tvOriginName.text = it.orgin.name
                            tvOriginUrl.text = it.orgin.url
                            tvLocationName.text = it.location.name
                            tvLocationUrl.text = it.location.url
                        }
                    }
                }
            }

            launch {
                characterInfoViewModel.viewEvent.collect {
                    when(it) {
                        is CharacterInfoViewEvent.Error -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        }
    }

}
