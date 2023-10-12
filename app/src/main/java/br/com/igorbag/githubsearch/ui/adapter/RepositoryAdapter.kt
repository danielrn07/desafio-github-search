package br.com.igorbag.githubsearch.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.databinding.RepositoryItemBinding
import br.com.igorbag.githubsearch.domain.Repository

class RepositoryAdapter : ListAdapter<Repository, RepositoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    var bodyItemList: (Repository) -> Unit = {}
    var btnShareLister: (Repository) -> Unit = {}

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Repository>() {
            override fun areItemsTheSame(oldItem: Repository, newItem: Repository): Boolean {
                return oldItem.name == newItem.name
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Repository, newItem: Repository): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            RepositoryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //@TODO 8 -  Realizar o bind do viewHolder

        val repository = getItem(position)

        holder.apply {
            binding.apply {
                tvRepositoryName.text = repository.name

                btnShare.setOnClickListener {
                    btnShareLister(repository)
                }

                body.setOnClickListener {
                    bodyItemList(repository)
                }
            }
        }
    }

    inner class MyViewHolder(val binding: RepositoryItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}