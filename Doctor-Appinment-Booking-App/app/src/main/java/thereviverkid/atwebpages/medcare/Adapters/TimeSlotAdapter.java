package thereviverkid.atwebpages.medcare.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import thereviverkid.atwebpages.medcare.R;

import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.ViewHolder> {

    private Context context;
    private List<String> slots;
    private List<String> bookedSlots;
    private int selectedPosition = -1;
    private OnSlotClickListener listener;

    public interface OnSlotClickListener {
        void onSlotClick(String slot);
    }

    public TimeSlotAdapter(Context context, List<String> slots, List<String> bookedSlots, OnSlotClickListener listener) {
        this.context = context;
        this.slots = slots;
        this.bookedSlots = bookedSlots;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_time_slot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String slot = slots.get(position);
        holder.timeText.setText(slot);

        boolean isBooked = bookedSlots.contains(slot);

        if (isBooked) {
            holder.card.setCardBackgroundColor(Color.parseColor("#F1F5F9"));
            holder.timeText.setTextColor(Color.parseColor("#94A3B8"));
            holder.statusText.setVisibility(View.VISIBLE);
            holder.statusText.setText("Booked");
            holder.statusText.setTextColor(Color.parseColor("#EF4444"));
            holder.card.setEnabled(false);
            holder.card.setStrokeColor(Color.TRANSPARENT);
        } else {
            holder.statusText.setVisibility(View.VISIBLE);
            holder.statusText.setText("Available");
            holder.statusText.setTextColor(ContextCompat.getColor(context, R.color.brand_secondary));
            
            if (selectedPosition == position) {
                holder.card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.brand_primary));
                holder.timeText.setTextColor(Color.WHITE);
                holder.statusText.setTextColor(Color.WHITE);
                holder.card.setStrokeColor(Color.WHITE);
            } else {
                holder.card.setCardBackgroundColor(Color.WHITE);
                holder.timeText.setTextColor(ContextCompat.getColor(context, R.color.text_heading));
                holder.card.setStrokeColor(Color.parseColor("#E2E8F0"));
            }
            
            holder.card.setEnabled(true);
            holder.card.setOnClickListener(v -> {
                int previousSelected = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);
                listener.onSlotClick(slot);
            });
        }
    }

    @Override
    public int getItemCount() {
        return slots.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView timeText, statusText;
        MaterialCardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeText = itemView.findViewById(R.id.tv_slot_time);
            statusText = itemView.findViewById(R.id.tv_status);
            card = itemView.findViewById(R.id.card_slot);
        }
    }
}
