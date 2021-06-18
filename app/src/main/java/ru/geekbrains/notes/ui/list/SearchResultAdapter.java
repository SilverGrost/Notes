package ru.geekbrains.notes.ui.list;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

import ru.geekbrains.notes.R;
import ru.geekbrains.notes.note.Note;

import static ru.geekbrains.notes.Constant.MILISECOND;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private final List<Note> notes;
    private final float textSize;

    public interface OnNoteClicked {
        void onNoteClickedList(View view, int position);
    }

    private OnNoteClicked noteClicked;

    public void SetOnNoteClicked(OnNoteClicked noteClicked) {
        this.noteClicked = noteClicked;
    }


    public interface OnDateClicked {
        void onDateClickedList(View view, int position);
    }

    private OnDateClicked dateClicked;

    public void SetOnDateClicked(OnDateClicked dateClicked) {
        this.dateClicked = dateClicked;
    }


    @Override
    public int getItemViewType(int position) {
        Log.v("Debug1", "SearchResultAdapter getItemViewType position = " + position);

        return 0;
    }

    // Передаем в конструктор источник данных
    // В нашем случае это массив, но может быть и запросом к БД
    public SearchResultAdapter(List<Note> notes, float textSize, String query) {
        Log.v("Debug1", "SearchResultAdapter SearchResultAdapter query = " + query);
        this.notes = notes;
        this.textSize = textSize;
    }

    // Создать новый элемент пользовательского интерфейса
    // Запускается менеджером
    @NonNull
    @Override
    public SearchResultAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // Создаем новый элемент пользовательского интерфейса
        // Через Inflater
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_note_by_rv, viewGroup, false);
        Log.v("Debug1", "SearchResultAdapter onCreateViewHolder i = " + i);
        // Здесь можно установить всякие параметры
        return new SearchResultAdapter.ViewHolder(v);
    }

    // Заменить данные в пользовательском интерфейсе
    // Вызывается менеджером
    @Override
    public void onBindViewHolder(@NonNull SearchResultAdapter.ViewHolder viewHolder, int i) {
        // Получить элемент из источника данных (БД, интернет...)
        // Вынести на экран используя ViewHolder
        Log.v("Debug1", "SearchResultAdapter onBindViewHolder i=" + i);
        viewHolder.setData(notes.get(i));

    }

    // Вернуть размер данных, вызывается менеджером
    @Override
    public int getItemCount() {
        return notes.size();
    }

    // Этот класс хранит связь между данными и элементами View
    // Сложные данные могут потребовать несколько View на
    // один пункт списка
    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewHeader;
        private final TextView textViewValuer;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.v("Debug1", "SearchResultAdapter class ViewHolder ");
            textViewHeader = itemView.findViewById(R.id.textViewTopRV);
            itemView.setTag(getAdapterPosition());
            // Обработчик нажатий на заголовке
            textViewHeader.setOnClickListener(v -> {
                if (dateClicked != null) {
                    dateClicked.onDateClickedList(v, getAdapterPosition());
                }
            });

            textViewValuer = itemView.findViewById(R.id.textViewBottomRV);
            // Обработчик нажатий на тексте
            textViewValuer.setOnClickListener(v -> {
                if (noteClicked != null) {
                    noteClicked.onNoteClickedList(v, getAdapterPosition());
                }
            });
        }

        public void setData(Note note) {
            Log.v("Debug1", "SearchResultAdapter class ViewHolder setData");
            long date = note.getDate() * MILISECOND;
            DateFormat f = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.getDefault());
            String dateStr = f.format(date);
            textViewHeader.setText(dateStr);
            textViewHeader.setTag(note.getID());
            textViewValuer.setText(note.getValue());
            textViewValuer.setTag(note.getID());

            textViewValuer.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        }
    }
}
