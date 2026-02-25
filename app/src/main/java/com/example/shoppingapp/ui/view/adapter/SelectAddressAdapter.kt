import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import com.example.shoppingapp.databinding.LayoutAddressItemBinding
import com.example.shoppingapp.domain.model.ghn.AddressGhn
import com.example.shoppingapp.domain.model.ghn.DistrictGhn
import com.example.shoppingapp.domain.model.ghn.ProvinceGhn
import com.example.shoppingapp.domain.model.ghn.WardGhn

class SelectAddressAdapter(
    context: Context,
    private var allAddresses: List<AddressGhn>,
) : ArrayAdapter<AddressGhn>(context, 0, allAddresses) {

    // Danh sách đã lọc để hiển thị
    private var filteredList: List<AddressGhn> = allAddresses

    override fun getCount(): Int = filteredList.size

    override fun getItem(position: Int): AddressGhn = filteredList[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: LayoutAddressItemBinding
        val itemView: View

        if (convertView == null) {
            binding = LayoutAddressItemBinding.inflate(LayoutInflater.from(context), parent, false)
            itemView = binding.root
            itemView.tag = binding
        } else {
            itemView = convertView
            binding = itemView.tag as LayoutAddressItemBinding
        }

        val address = getItem(position)
        address.let {
            when (it) {
                is ProvinceGhn -> binding.tvAddress.text = it.provinceName
                is DistrictGhn -> binding.tvAddress.text = it.districtName
                is WardGhn -> binding.tvAddress.text = it.wardName
            }
        }

        return itemView
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            //lọc để tìm kiếm khi gõ chữ
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""

                filteredList = if (charString.isEmpty()) {
                    allAddresses
                } else {
                    allAddresses.filter {
                        // Lọc theo tên địa chỉ (không phân biệt hoa thường)
                        when (it) {
                            is ProvinceGhn -> it.provinceName.lowercase()
                                .contains(charString.lowercase())

                            is DistrictGhn -> it.districtName.lowercase()
                                .contains(charString.lowercase())

                            is WardGhn -> it.wardName.lowercase().contains(charString.lowercase())
                            else -> true
                        }
                    }
                }

                return FilterResults().apply {
                    values = filteredList
                    count = filteredList.size
                }
            }

            //sau khi performFiltering lọc xong thì có data và ở đây sẽ có nhiệm vụ hiển thị data đó lên view
            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as? List<AddressGhn> ?: listOf()
                if ((results?.count ?: 0) > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }

            // trả về chuỗi hiển thị lên view sau khi chọn item
            override fun convertResultToString(resultValue: Any?): CharSequence {
                val addressGhn = (resultValue as? AddressGhn) ?: return ""
                return when (addressGhn) {
                    is ProvinceGhn -> addressGhn.provinceName
                    is DistrictGhn -> addressGhn.districtName
                    is WardGhn -> addressGhn.wardName
                    else -> ""
                }

            }
        }
    }

    fun setData(newList: List<AddressGhn>) {
        this.allAddresses = newList
        this.filteredList = newList
        notifyDataSetChanged()
    }

}