package neobis.alier.poputchik.ui.list

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_list.view.*
import neobis.alier.poputchik.R
import neobis.alier.poputchik.model.Info

/**
 * Created by Alier on 03.04.2018.
 */
class ListAdapter(private var list: MutableList<Info>,
                  val listener: Listener) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setList(newList: MutableList<Info>) {
        this.list = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        fun bind(model: Info) {
            itemView.time.text = itemView.context.getString(R.string.time, model.start_time)
            itemView.from.text = itemView.context.getString(R.string.start_addr, model.start_address)
            itemView.toWhere.text = itemView.context.getString(R.string.end_addr, model.end_address)

            itemView.call.setOnClickListener { v ->
                val phone = v.tag as? String?
                if (!TextUtils.isEmpty(phone))
                    listener.onCallUser(phone!!)
            }
            itemView.call.tag = model.phone
            itemView.parentView.tag = model
            itemView.parentView.setOnClickListener { v ->
                val tag = v.tag as? Info?
                if (tag != null) {
                    listener.onClick(model)
                }
            }
        }
    }

    interface Listener {
        fun onClick(model: Info?)
        fun onCallUser(phone: String)
    }
}