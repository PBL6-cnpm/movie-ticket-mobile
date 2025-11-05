package com.pbl6.pbl6_cinestech.ui.fullvideo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pbl6.pbl6_cinestech.databinding.FragmentFullScreenVideoBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


class FullScreenVideoFragment : Fragment() {
    private lateinit var binding: FragmentFullScreenVideoBinding
    private lateinit var youtubePlayerView: YouTubePlayerView
    private val videoId: String? by lazy {
        arguments?.getString(ARG_VIDEO_ID)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFullScreenVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("load video", "onViewCreated: video $videoId")
        youtubePlayerView = binding.youtubePlayerView
        lifecycle.addObserver(youtubePlayerView)

        youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                videoId?.let {
                    youTubePlayer.loadVideo(it, 0f)
                }
            }
        })
    }
    companion object {
        private const val ARG_VIDEO_ID = "video_id"
        fun newInstance(videoId: String) = FullScreenVideoFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_VIDEO_ID, videoId)
            }
        }
    }
}