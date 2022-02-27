
package ru.netology.nmedia.adapter
import Wallsevice
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardTimingBinding
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.Timing
import ru.netology.nmedia.view.load
import ru.netology.nmedia.view.loadCircleCrop
<<<<<<< HEAD
=======

>>>>>>> 49f0ae467f8392f994cf9a5438ae89fe8845fe9c


class FeedAdapter(
    private val onInteractionListener: OnInteractionListener,
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(FeedItemDiffCallback()) {
    private val typeTiming =0
    private val typeAd = 1
    private val typePost = 2

    interface OnInteractionListener {
        fun onLike(post: Post) {}
        fun onEdit(post: Post) {}
        fun onRemove(post: Post) {}
        fun onShare(post: Post) {}
        fun pushPhoto (post: Post) {}
        fun onAdClick(ad: Ad) {}
        fun onTimingClick(timing: Timing){}
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Ad -> typeAd
            is Timing ->typeTiming
            is Post -> typePost

            null -> throw IllegalArgumentException("unknown item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            typeAd -> AdViewHolder(
                CardAdBinding.inflate(layoutInflater, parent, false),
                onInteractionListener
            )
            typePost -> PostViewHolder(
                CardPostBinding.inflate(layoutInflater, parent, false),
                onInteractionListener)

            typeTiming -> TimingViewHolder(
                CardTimingBinding.inflate(layoutInflater, parent, false),
                onInteractionListener

            )
            else -> throw IllegalArgumentException("unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // FIXME: students will do in HW
        getItem(position)?.let {
            when (it) {
                is Post -> (holder as? PostViewHolder)?.bind(it)
                is Ad  -> (holder as? AdViewHolder)?.bind(it )

                is Timing -> (holder as? TimingViewHolder)?.bind(it)
            }
        }
    }


//  override  fun onBindViewHolder(
//        holder:  RecyclerView.ViewHolder,
//        position: Int,
//        payloads: List<Any>
//    ) {
//        if (payloads.isEmpty()) {
//            onBindViewHolder(holder, position)
//        } else {
//            payloads.forEach {
//                if (it is Payload) {
//                    (holder as PostViewHolder).bind(it as Post)
//
//                }
//            }
//        }
//    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: List<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            payloads.forEach {
                if (it is Payload) {
                    (holder as PostViewHolder).bind(it)

                }
            }
        }
    }


    class PostViewHolder(
        private val binding: CardPostBinding,
        private val onInteractionListener: OnInteractionListener,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(payload: Payload,) {
            payload.liked?.also { liked ->
                binding.like.isChecked = liked
                // Вы можете добавить количество лайков в Payload, если нужно
                //   binding. like.text = "${service.zeroingOutLikes(post.likes.toLong())}"
                payload.likedText?.also {
                    binding.like.text = it.toString()

                    if (liked) {
                        ObjectAnimator.ofPropertyValuesHolder(

                            binding.like,
                            PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0F, 1.2F, 1.0F, 1.2F),
                            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0F, 1.2F, 1.0F, 1.2F)
                        ).start()
                    } else {
                        ObjectAnimator.ofFloat(
                            binding.like,
                            View.ROTATION,
                            0F, 360F
                        ).start()
                    }
                }

            }

            payload.content?.let(binding.content::setText)
        }

        val service = Wallsevice()
        fun bind(post: Post) {
            binding.apply {
                author.text = post.author
                published.text = post.published.toString()
                content.text = post.content
                avatar.loadCircleCrop("${BuildConfig.BASE_URL}/avatars/${post.authorAvatar}")
                like.isChecked = post.likedByMe
                like.text = "${service.zeroingOutLikes(post.likes.toLong())}"

                //   share.isChecked
                //share.text = "${service.zeroingOutShare(post.share.toLong())}"

                photo.setImageURI(Uri.parse( "${BuildConfig.BASE_URL}/attachment/моя_картинка.jpg"))

                photo.isVisible = post.attachment != null
                post.attachment?.let {
                    Log.d("MyLog", "${BuildConfig.BASE_URL}/media/${it.url}")
                    Glide.with(photo)
                        .load("${BuildConfig.BASE_URL}/media/${it.url}")
                        .timeout(10_000)
                        .into(photo)
                }


                menu.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE

                menu.setOnClickListener {

                    PopupMenu(it.context, it).apply {

                        inflate(R.menu.options_post)

                        menu.setGroupVisible(R.id.owned, post.ownedByMe)

                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.remove -> {
                                    onInteractionListener.onRemove(post)
                                    true
                                }
                                R.id.edit -> {
                                    onInteractionListener.onEdit(post)
                                    true
                                }

                                else -> false
                            }
                        }
                    }.show()
                }

                like.setOnClickListener {
                    onInteractionListener.onLike(post)
                }

                share.setOnClickListener {
                    onInteractionListener.onShare(post)
                }

                photoContainer.setOnClickListener {
                    onInteractionListener.pushPhoto(post)
                }

            }
        }
    }

    class AdViewHolder(
        private val binding: CardAdBinding,
        private val onInteractionListener: OnInteractionListener,
    ) : RecyclerView.ViewHolder(binding.root) {


        val service = Wallsevice()

        fun bind(ad: Ad) {
            binding.apply {
                image.load("${BuildConfig.BASE_URL}/media/${ad.image}")

                timing.text =  "${service.timeСonverter(ad.timing)}"
                image.setOnClickListener {
                    onInteractionListener.onAdClick(ad)
                }
<<<<<<< HEAD
=======
<<<<<<< HEAD
                timing.text =  "${service.timeСonverter()}"
=======
>>>>>>> 49f0ae467f8392f994cf9a5438ae89fe8845fe9c
               // timing.text =  "${service.agoToText}"
>>>>>>> efee44face09fa4eefa0db16c6b2ac97051d11e6
            }
        }
    }

    class  TimingViewHolder(
        private val binding: CardTimingBinding,
        private val onInteractionListener: OnInteractionListener,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(timings: Timing) {
            binding.apply {
                val service = Wallsevice()
<<<<<<< HEAD
                timing.text =  "${service.timeСonverter()}"
           //   timing.text =  "${service.agoToText}"
=======

<<<<<<< HEAD
                timing.text =  "${service.timeСonverter(timings.timing)}"
=======
              //  timing.text =  "${service.agoToText}"
>>>>>>> 49f0ae467f8392f994cf9a5438ae89fe8845fe9c
>>>>>>> efee44face09fa4eefa0db16c6b2ac97051d11e6

                timing.setOnClickListener {
                    onInteractionListener.onTimingClick(timings)
                }
            }
        }
    }


     class FeedItemDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
        override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
            if (oldItem::class != newItem::class) {
                return false
            }

            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: FeedItem, newItem: FeedItem): Any? =
            if (oldItem is Post && newItem is Post) {
                Payload(
                    liked = newItem.likedByMe.takeIf { oldItem.likedByMe != it },
                    content = newItem.content.takeIf { oldItem.content != it },
                    likedText =newItem.likes.takeIf{oldItem.likes !=it},
                    )
            } else {
                null
            }

    }

}

data class Payload(
    val liked: Boolean? = null,
    val content: String? = null,
    val likedText: Int? = null
    )



