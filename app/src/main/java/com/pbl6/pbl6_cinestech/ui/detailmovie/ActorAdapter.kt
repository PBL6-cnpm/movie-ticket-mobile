package com.pbl6.pbl6_cinestech.ui.detailmovie

import com.pbl6.pbl6_cinestech.data.model.response.Actor
import com.pbl6.pbl6_cinestech.databinding.ItemActorBinding
import hoang.dqm.codebase.base.adapter.BaseRecyclerViewAdapter
import hoang.dqm.codebase.utils.loadImageSketch

class ActorAdapter: BaseRecyclerViewAdapter<Actor, ItemActorBinding>() {
    override fun bindData(
        binding: ItemActorBinding,
        item: Actor,
        position: Int
    ) {
        binding.imgActor.loadImageSketch(item.picture)
        binding.tvNameActor.text = item.name
    }
}