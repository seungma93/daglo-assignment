package com.seungma.daglo.presenter.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seungma.daglo.databinding.ListItemCharacterBinding
import com.seungma.daglo.domain.list.entity.CharacterEntity


class CharactersListAdapter(
    private val itemClick: (CharacterEntity) -> Unit,
) : ListAdapter<CharacterEntity, CharactersListAdapter.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListItemCharacterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(
            binding,
            itemClick
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ListItemCharacterBinding,
        private val itemClick: (CharacterEntity) -> Unit,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private var characterEntity: CharacterEntity? = null

        init {
            binding.apply {
                root.setOnClickListener {
                    characterEntity?.let {
                        itemClick(it)
                    }
                }
            }

        }

        fun bind(characterEntity: CharacterEntity) {
            this.characterEntity = characterEntity
            binding.apply {
                Glide.with(itemView.context)
                    .load(characterEntity.image)
                    .into(ivImage)
                tvName.text = characterEntity.name
                tvGender.text = characterEntity.gender
                tvStatus.text = characterEntity.status
                tvId.text = characterEntity.id.toString()
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<CharacterEntity>() {

            // 두 아이템이 동일한 아이템인지 체크. 보통 고유한 id를 기준으로 비교
            override fun areItemsTheSame(
                oldItem: CharacterEntity,
                newItem: CharacterEntity
            ): Boolean {
                return oldItem.id == newItem.id
            }

            // 두 아이템이 동일한 내용을 가지고 있는지 체크. areItemsTheSame()이 true일때 호출됨
            override fun areContentsTheSame(
                oldItem: CharacterEntity,
                newItem: CharacterEntity
            ): Boolean {
                return oldItem == newItem
            }

        }
    }


}