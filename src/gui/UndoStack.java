package gui;

import gui.Action.ActionType;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import model.IController;

public class UndoStack
{
	private IController controller;
	private ObservableList<Action> actions;
	private ListView<Action> undostackView;			// may be null

	int curStackPtr = 0; //  or number of actions available to redo
	int verbose = 3;

	// -------------------------------------------------------
	public UndoStack(IController c, ListView<Action> undoview)
	{
		if (verbose > 0)
			System.out.println("created undo stack");
		controller = c;
		actions = FXCollections.observableArrayList();
		actions.addListener(new ListChangeListener<Action>()
		{
			@Override public void onChanged(Change<? extends Action> c)	{	refresh();	}
		});
		undostackView = undoview;
		if (undostackView != null)
			undostackView.setItems(actions);
	}

	// -------------------------------------------------------
	public void undo()
	{
		if (curStackPtr >= actions.size()) return;
		Action a = currentAction();
		if (a != null)
		{
			a.undo();
			String savedState = a.getState();
			controller.setState(savedState);
			actions.set(curStackPtr, a);
			curStackPtr++;
			refresh();
		}
	}

	public void redo()
	{
		if (curStackPtr > 0)
		{
			curStackPtr--;
			Action a = actions.get(curStackPtr);
			if (a != null)
			{
				a.doIt();
				actions.set(curStackPtr, a);
//				controller.setState(a.getState());
				refresh();
			}
		}
	}

	// -------------------------------------------------------
	// key method:  grab the entire state of the drawing into the action, 
	//	and push the action onto the top (index = 0) of the undo stack
	public void push(ActionType actionType)
	{
		push(actionType, "");
	}	
	public void push(ActionType actionType, String text)
	{
		Log.action(actionType);
		while (actions.size() > 0)				// throw away undone Actions
		{
			Action a = actions.get(0);
			if (a.isUndone())
				actions.remove(0);				// normally its bad to remove items from the front!
			else break;
		}
		curStackPtr = 0;						// reset stack pointer
		Action action = new Action(actionType);
		action.doIt(); // this only sets the state flag
		action.setController(controller);
		action.saveState();
//		action.setSelection(controller.getSelection()); // this is redundant since
						// we save the full state, but is a future optimization
		actions.add(0, action);
		action.setText(text);
		if (verbose > 0)
			System.out.println(action.getText() + ": \n" + action.getState());
//		controller.reportStatus("Push: " + action.getText() + " " + actions.size());
	}

	// -------------------------------------------------------
	private void refresh()				{	if (undostackView != null)	undostackView.setItems(actions);	}
	private Action currentAction()		{		return actions.get(curStackPtr);	}

	public void clear()
	{
		actions.clear();
		curStackPtr = 0;
		if (undostackView != null)	
			undostackView.getItems().clear();
//		push(ActionType.New);
	}
}
