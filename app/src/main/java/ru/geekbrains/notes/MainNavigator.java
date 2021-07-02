package ru.geekbrains.notes;

import androidx.fragment.app.FragmentManager;

import ru.geekbrains.notes.ui.item.ViewNoteFragment;


public class MainNavigator {

    private final FragmentManager fragmentManager;

    public MainNavigator(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void showNotes(int noteID) {
        fragmentManager
                .beginTransaction()
                .replace(R.id.container, ViewNoteFragment.newInstance(noteID), ViewNoteFragment.TAG)
                .commit();
    }

    /*public void showAuth() {
        fragmentManager
                .beginTransaction()
                .replace(R.id.container, AuthFragment.newInstance(), AuthFragment.TAG)
                .commit();
    }

    public void showInfo() {
        fragmentManager
                .beginTransaction()
                .replace(R.id.container, InfoFragment.newInstance(), InfoFragment.TAG)
                .commit();
    }

    public void showNoteDetail(Note note) {
        fragmentManager
                .beginTransaction()
                .replace(R.id.container, NoteDetailsFragment.newInstance(note), NoteDetailsFragment.TAG)
                .addToBackStack(NoteDetailsFragment.TAG)
                .commit();
    }


    public void showEditNote(Note note) {
        fragmentManager
                .beginTransaction()
                .replace(R.id.container, UpdateNoteFragment.newInstance(note), UpdateNoteFragment.TAG)
                .addToBackStack(UpdateNoteFragment.TAG)
                .commit();
    }*/

    public void back() {
        fragmentManager.popBackStack();
    }

}
