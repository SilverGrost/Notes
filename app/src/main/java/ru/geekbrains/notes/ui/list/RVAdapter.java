package ru.geekbrains.notes.ui.list;

import android.content.res.Configuration;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

import ru.geekbrains.notes.Settings;
import ru.geekbrains.notes.note.Note;

import ru.geekbrains.notes.R;

import static ru.geekbrains.notes.Constant.MILISECOND;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    private final List<Note> notes;
    private final float textSize;
    private final int sortType;
    private final int maxCountLines;
    private final int orientation;
    private final Fragment fragment;

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

    private int menuPosition;

    public int getMenuPosition() {
        return menuPosition;
    }


    // Передаем в конструктор источник данных
    // В нашем случае это массив, но может быть и запросом к БД
    public RVAdapter(List<Note> notes, Settings settings, Fragment fragment, int orientation) {
        Log.v("Debug1", "NotesListAdapter ListNotesAdapter notes.size()=" + notes.size());
        this.notes = notes;
        this.textSize = settings.getTextSize();
        this.sortType = settings.getOrderType();
        this.maxCountLines = settings.getMaxCountLines();
        this.orientation = orientation;
        this.fragment = fragment;
    }

    // Создать новый элемент пользовательского интерфейса
    // Запускается менеджером
    @NonNull
    @Override
    public RVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // Создаем новый элемент пользовательского интерфейса
        // Через Inflater
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_note_by_rv, viewGroup, false);
        Log.v("Debug1", "NotesListAdapter onCreateViewHolder i=" + i);
        // Здесь можно установить всякие параметры
        return new ViewHolder(v);
    }

    // Заменить данные в пользовательском интерфейсе
    // Вызывается менеджером
    @Override
    public void onBindViewHolder(@NonNull RVAdapter.ViewHolder viewHolder, int i) {
        // Получить элемент из источника данных (БД, интернет...)
        // Вынести на экран используя ViewHolder
        viewHolder.setData(notes.get(i));
        Log.v("Debug1", "NotesListAdapter onBindViewHolder i=" + i + ", notes.get(i).getID()=" + notes.get(i).getID());
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
        private final ImageView imageForPopupMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.v("Debug1", "NotesListAdapter class ViewHolder ");
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
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {

                        switch (maxCountLines){
                            case (-1):  //Без ограничений
                                textViewValuer.setMaxLines(Integer.MAX_VALUE);
                                break;
                            case (0):   //Авторазвёртывание в списке
                                if (textViewValuer.getMaxLines() != Integer.MAX_VALUE) {
                                    textViewValuer.setMaxLines(Integer.MAX_VALUE);
                                } else {
                                    textViewValuer.setMaxLines(1);
                                }
                                break;
                            default:    //Выбранное кол-во
                                textViewValuer.setMaxLines(maxCountLines);
                                noteClicked.onNoteClickedList(v, ViewHolder.this.getAdapterPosition());
                        }
                    } else
                        noteClicked.onNoteClickedList(v, ViewHolder.this.getAdapterPosition());
                }
            });

            // Обработчик нажатий на иконке меню
            imageForPopupMenu = (itemView.findViewById(R.id.imageRVForPopupMenu));
            if (imageForPopupMenu != null)
                registerContextMenu(imageForPopupMenu);
        }

        private void registerContextMenu(@NonNull View itemView) {
            if (fragment != null) {
                imageForPopupMenu.setOnClickListener(v -> {
                    imageForPopupMenu.showContextMenu(0, 0);
                    menuPosition = (int) imageForPopupMenu.getTag();
                });
                fragment.registerForContextMenu(itemView);
            }
        }

        public void setData(Note note) {
            Log.v("Debug1", "NotesListAdapter class ViewHolder setData sortType=" + sortType);
            long date = note.getDateCreate() * MILISECOND;
            if (sortType == 0 | sortType == 1)
                date = note.getDateEdit() * MILISECOND;
            DateFormat f = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.getDefault());
            String dateStr = f.format(date);

            textViewHeader.setText(dateStr);
            textViewHeader.setTag(note.getID());

            textViewValuer.setText(note.getValue());
            textViewValuer.setTag(note.getID());
            textViewValuer.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                switch (maxCountLines){
                    case (-1):  //Без ограничений
                        textViewValuer.setMaxLines(Integer.MAX_VALUE);
                        break;
                    case (0):   //Авторазвёртывание в списке
                        textViewValuer.setMaxLines(1);
                        break;
                    default:    //Выбранное кол-во
                        textViewValuer.setMaxLines(maxCountLines);
                }
            } else
                textViewValuer.setMaxLines(1);

            if (imageForPopupMenu != null)
                imageForPopupMenu.setTag(note.getID());

        }
    }
}
