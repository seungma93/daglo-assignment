package com.seungma.daglo.presenter.list.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.seungma.daglo.databinding.FragmentCharacterBinding
import com.seungma.daglo.domain.list.entity.CharacterItemKeyEntity

class CharacterFragment : Fragment() {
    companion object {
        const val CHARACTER_ITEM_KEY = "CHARACTER_ITEM_KEY"

        fun newInstance(
            characterItemKeyEntity: CharacterItemKeyEntity
        ): CharacterFragment {
            return CharacterFragment().apply {
                arguments = bundleOf(
                    CHARACTER_ITEM_KEY to characterItemKeyEntity
                )
            }
        }
    }

    private val characterItemKeyEntity
        get() = requireArguments().getSerializable(
            CHARACTER_ITEM_KEY
        ) as CharacterItemKeyEntity

    private var _binding: FragmentCharacterBinding? = null
    private val binding get() = _binding!!

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

        binding.apply {
            characterItemKeyEntity.characterEntity.let {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
