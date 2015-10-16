/*
 * @(#)ValidationUtils.java 5/19/2013
 *
 * Copyright 2002 - 2013 JIDE Software Inc. All rights reserved.
 */

package validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Cell;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.stage.Window;
import util.CommonUtils;
import util.TriFunction;

public class ValidationUtils {
	static ValidationUtils instance = new ValidationUtils();

	public enum ValidationMode 		{  ON_FLY,   ON_FOCUS_LOST	}	
	public enum ValidationStatus 	{   VALIDATION_OK, VALIDATION_INFO,	VALIDATION_WARNING,	 VALIDATION_ERROR,	VALIDATION_UNKNOWN	}
//	public enum ValidationDecorationType {    FONT_AWESOME,   GRAPHIC  }

    public static class TooltipFix extends Tooltip {

        private Node parentNode;

        public TooltipFix(Node parentNode) {          this.parentNode = parentNode;   }

        @Override     public Styleable getStyleableParent() {
            Styleable styleableParent = super.getStyleableParent();
            if (styleableParent != null)         return styleableParent;
            return parentNode;
        }
    }

    private static final String PROPERTY_ON_FLY_VALIDATOR = "Validation.On.Fly.Validator"; //NON-NLS
    private static final String PROPERTY_ON_FLY_OBSERVABLE_VALUE = "Validation.On.Fly.ObservableValue"; //NON-NLS
    private static final String PROPERTY_ON_FLY_LISTENER = "Validation.On.Fly.Listener"; //NON-NLS
    private static final String PROPERTY_ON_FLY_EVENT_FILTER = "Validation.On.Fly.EventFilter"; //NON-NLS

    private static final String PROPERTY_ON_FOCUS_LOST_VALIDATOR = "Validation.On.FocusLost.Validator"; //NON-NLS
    private static final String PROPERTY_ON_FOCUS_LOST_LISTENER = "Validation.On.FocusLost.Listener"; //NON-NLS
    private static final String PROPERTY_ON_FOCUS_LOST_EVENT_FILTER = "Validation.On.FocusLost.EventFilter"; //NON-NLS
    private static final String PROPERTY_ON_FOCUS_LOST_OBJECT = "Validation.On.FocusLost.Object"; //NON-NLS

    private static final String PROPERTY_VALIDATION_RESULT = "Validation.Result";				 //NON-NLS
    private static final String PROPERTY_VALIDATION_RESULT_MESSAGE = "Validation.Result.Message"; //NON-NLS

    public static final String PSEUDO_CLASS_VALIDATION_ERROR = "validation-error"; //NON-NLS
    public static final String PSEUDO_CLASS_VALIDATION_WARNING = "validation-warning"; //NON-NLS
    public static final String PSEUDO_CLASS_VALIDATION_INFO = "validation-info"; //NON-NLS
    public static final String PSEUDO_CLASS_VALIDATION_OK = "validation-ok"; //NON-NLS

    /**
     * Installs a validator to a target node. If there is an old validator, it will be uninstalled first. A default
     * observable value will be used for the give node. For example, textProperty of a TextInputControl or its
     * subclasses, selectedItemProperty of the SelectionModel for ListView, TableView, TreeView, ChoiceBox, and
     * ComboBox, selectedProperty for CheckBox and RadioButton. A default event handler will be installed on the node to
     * display the validation result.
     *
     * @param targetNode the target node where the validator will be installed
     * @param validator  the validator
     * @param mode       the validation mode
     * @param <T>        the date type of the observable value.
     * @return true or false. True if the validator is installed correctly. It actually always return true as long as
     * the observable value and the validator are not null.
     */
    @SuppressWarnings("unchecked") 
    
    public static <T> boolean install(Node targetNode, Validator validator, ValidationMode mode) {
    	EventHandler<ValidationEvent> eventFilter =null;
    	return install(targetNode, getDefaultObservableValue(targetNode), validator, mode, eventFilter);
    }
    /**
     * Installs a validator to a target node. If there is an old validator, it will be uninstalled first.
     *
     * @param targetNode      the target node where the validator will be installed
     * @param observableValue the observable value to listen to if the validation mode is ON_FLY. In the other two
     *                        modes, there is where the value to be validated is retrieved
     * @param validator       the validator
     * @param mode            the validation mode
     * @param eventFilter     the event handler. It will be added to the target node. When there is a validation event,
     *                        this handler will response to it and display the validation result.
     * @param <T>             the date type of the observable value.
     * @return true or false. True if the validator is installed correctly. It actually always return true as long as
     * the observable value and the validator are not null.
     */
    public static <T> boolean install(Node targetNode, ObservableValue<T> observableValue, Validator validator, ValidationMode mode, EventHandler<ValidationEvent> eventFilter) {
        if (observableValue == null || validator == null)        return false;

        switch (mode) {
            case ON_FOCUS_LOST:   return setOnFocusLostValidation(targetNode, validator, observableValue, eventFilter);
            case ON_FLY:          return setOnFlyValidation(targetNode, validator, observableValue, eventFilter);
        }
        return false;
    }
//
//    public static <T> boolean install(Node targetNode, ObservableValue<T> observableValue, Validator validator, ValidationMode mode, TriFunction<Node, Decorator, ValidationEvent, Decorator<Label>> decorationCreator) {
//        return install(targetNode, observableValue, validator, mode, createDefaultValidationEventHandler(targetNode, decorationCreator));
//    }

    // TODO: what if the targetNode is a TableView, ListView or a TreeView. Can the validator work as well when the validation is for an editing cell??
  // I only care to validate text fields!!!
    
    private static ObservableValue getDefaultObservableValue(Node targetNode) {
        ObservableValue observableValue = null;
        Class clazz = targetNode.getClass();
        if (TextInputControl.class.isAssignableFrom(clazz))  return ((TextInputControl) targetNode).textProperty();
        return observableValue;
    }
    private static ValidationMode getDefaultMode() {        return ValidationMode.ON_FLY;    }

    private static <T> boolean setOnFlyValidation(Node targetNode, Validator validator, ObservableValue<T> targetProperty, EventHandler<ValidationEvent> eventFilter) {
        uninstall(targetNode, ValidationMode.ON_FLY);

        ChangeListener<T> listner = (obs, old, newval) -> {
            ValidationObject validationObject = instance.new ValidationObject(targetNode, old, newval);
            ValidationEvent event = validator.call(validationObject);
            targetNode.fireEvent(event);
        };
        targetProperty.addListener(listner);
        targetNode.addEventFilter(ValidationEvent.ANY, eventFilter);

        ObservableMap<Object, Object> prop = targetNode.getProperties();
        prop.put(PROPERTY_ON_FLY_VALIDATOR, validator);
        prop.put(PROPERTY_ON_FLY_OBSERVABLE_VALUE, targetProperty);
        prop.put(PROPERTY_ON_FLY_EVENT_FILTER, eventFilter);
        prop.put(PROPERTY_ON_FLY_LISTENER, listner);
        return true;
    }

    private static <T> boolean setOnFocusLostValidation(Node targetNode, Validator validator, ObservableValue<T> targetProperty, EventHandler<ValidationEvent> eventFilter) {
        uninstall(targetNode, ValidationMode.ON_FOCUS_LOST);

        ChangeListener<Boolean> listener =  (obs, old, newval) -> {
            if (newval) { // focus gained
                targetNode.getProperties().put(PROPERTY_ON_FOCUS_LOST_OBJECT, targetProperty.getValue()); // save old value
            } else 
            { // focus lost
                Object oldValidationValue = targetNode.getProperties().get(PROPERTY_ON_FOCUS_LOST_OBJECT);
                T newValidationValue = targetProperty.getValue();
                if (!CommonUtils.equals(oldValidationValue, newValidationValue)) {
                    ValidationObject validationObject = instance.new ValidationObject(targetNode, oldValidationValue, newValidationValue);
                    ValidationEvent event = validator.call(validationObject);
                    targetNode.fireEvent(event);
                }
            }
        };
        targetNode.focusedProperty().addListener(listener);
        targetNode.addEventFilter(ValidationEvent.ANY, eventFilter);
        targetNode.getProperties().put(PROPERTY_ON_FOCUS_LOST_VALIDATOR, validator);
        targetNode.getProperties().put(PROPERTY_ON_FOCUS_LOST_EVENT_FILTER, eventFilter);
        targetNode.getProperties().put(PROPERTY_ON_FOCUS_LOST_LISTENER, listener);
        targetNode.getProperties().put(PROPERTY_ON_FOCUS_LOST_OBJECT, targetProperty.getValue());

        return true;
    }

    /**
     * Uninstalls the validators from the target node for all validation modes.
     *
     * @param targetNode the node to be validated.
     * @return true if uninstalled successfully. Otherwise false.
     */
    public static boolean uninstall(Node targetNode) {
        return uninstall(targetNode, ValidationMode.ON_FLY) && 
        		uninstall(targetNode, ValidationMode.ON_FOCUS_LOST);
    }

    /**
     * Uninstalls the validator from the target node for the given validation mode.
     *
     * @param targetNode the node to be validated.
     * @param mode       the validation mode.
     * @return true if uninstalled successfully. If the target node is null or there is no validators installed, it will
     * return false. Please be noted that all validators will be cleared no matter what is returned.
     */
    @SuppressWarnings("unchecked")
    public static boolean uninstall(Node targetNode, ValidationMode mode) {
        if (targetNode == null)             return false;
        
        ValidationMode removeMode = mode;
        if (removeMode == null) 
            removeMode = getDefaultMode();
    	ObservableMap<Object, Object> props = targetNode.getProperties();
    
        switch (removeMode) {
            case ON_FOCUS_LOST: {
                Object eventFilter = props.get(PROPERTY_ON_FOCUS_LOST_EVENT_FILTER);
                if (eventFilter instanceof EventHandler) 
                    targetNode.removeEventFilter(ValidationEvent.ANY, (EventHandler<ValidationEvent>) eventFilter);
                

                Object remove = props.remove(PROPERTY_ON_FOCUS_LOST_VALIDATOR);
                if (remove != null) {
                	props.remove(PROPERTY_ON_FOCUS_LOST_LISTENER);
                	props.remove(PROPERTY_ON_FOCUS_LOST_EVENT_FILTER);
                	props.remove(PROPERTY_ON_FOCUS_LOST_OBJECT);

                    Object listener = props.get(PROPERTY_ON_FOCUS_LOST_LISTENER);
                    if (listener instanceof ChangeListener) 
                        targetNode.focusedProperty().removeListener((ChangeListener) listener);
                    else return false;
                } else return false;
            }
            break;
            case ON_FLY:
            default: {
                Object eventFilter = props.get(PROPERTY_ON_FLY_EVENT_FILTER);
                if (eventFilter instanceof EventHandler) 
                    targetNode.removeEventFilter(ValidationEvent.ANY, (EventHandler<ValidationEvent>) eventFilter);
                

                Object remove = props.remove(PROPERTY_ON_FLY_VALIDATOR);
                if (remove != null) {
                	props.remove(PROPERTY_ON_FLY_OBSERVABLE_VALUE);
                	props.remove(PROPERTY_ON_FLY_LISTENER);
                	props.remove(PROPERTY_ON_FLY_EVENT_FILTER);

                    Object onFlyValue = props.get(PROPERTY_ON_FLY_OBSERVABLE_VALUE);
                    Object onFlyListener = props.get(PROPERTY_ON_FLY_LISTENER);
                    if (onFlyValue instanceof ObservableValue && onFlyListener instanceof ChangeListener) {
                        ((ObservableValue) onFlyValue).removeListener((ChangeListener) onFlyListener);
                    } else return false;
                } else return false;

            }
            break;
        }
        return true;
    }

    /**
     * Force the validator to execute validation immediately when there is no correspond event. Should be used ON_FLY
     * and ON_FOCUS_LOST validation modes.
     *
     * @param validateNode the validate node
     * @param mode         the validation mode, ON_FLY or ON_FOCUS_LOST.
     */
    public static void forceValidate(Node validateNode, ValidationMode mode) {
        Object validator;
        ObservableValue<?> observableValue;
    	ObservableMap<Object, Object> props = validateNode.getProperties();

        switch (mode) {
            case ON_FLY:
                validator = props.get(PROPERTY_ON_FLY_VALIDATOR);
                observableValue = (ObservableValue<?>) props.get(PROPERTY_ON_FLY_OBSERVABLE_VALUE);
                if (validator instanceof Validator) {
                    ValidationObject object = instance.new ValidationObject(validateNode, null, observableValue.getValue());
                    ValidationEvent event = ((Validator) validator).call(object);
                    validateNode.fireEvent(event);
                }
                break;
            case ON_FOCUS_LOST:
                validator = props.get(PROPERTY_ON_FLY_VALIDATOR);
                observableValue = (ObservableValue<?>) props.get(PROPERTY_ON_FOCUS_LOST_OBJECT);
                if (validator instanceof Validator) {
                    ValidationObject object = instance.new ValidationObject(validateNode, null, observableValue);
                    ValidationEvent event = ((Validator) validator).call(object);
                    validateNode.fireEvent(event);
                }
                break;
        }
    }

 
    public static boolean isValidationDecorator(Decorator decorator) {
        if (Optional.of(decorator.getNode()).isPresent()) {
            Node _node = Optional.of((decorator).getNode()).get();
            if (_node instanceof Label) {
                Label validationLabel = (Label) _node;		  // Search for validation label
                long count = validationLabel.getStyleClass().stream().filter(styleClass -> styleClass.equals(ValidationDecorators.PROPERTY_VALIDATION_DECORATOR)).count();
                if (count > 0)         return true;
            }
        }
        return false;
    }

    public static Optional<List<Decorator>> getValidationDecorators(Node node) {
        if (Optional.ofNullable(DecorationUtils.getDecorators(node)).isPresent()) {
            Object o = Optional.of(DecorationUtils.getDecorators(node)).get();

            if (o instanceof Decorator && isValidationDecorator((Decorator) o)) 
                    return Optional.of(new ArrayList<Decorator>() { {    add((Decorator) o); } });
            	 
            if (o instanceof Decorator[]) {
                Decorator[] decorators = (Decorator[]) o;
                List<Decorator> validationLabels = new ArrayList<>();

                for (int i = 0; i < decorators.length; i++) {
                    Decorator d = decorators[i];
                    if (isValidationDecorator(d))         validationLabels.add(d);
                }
                return Optional.of(validationLabels);
            }
        }
        return Optional.empty();
    }

    public static void showTooltip(Label label) {
        if (label != null && label.getTooltip() != null) {
            Point2D point = label.localToScene(15.0, 15.0);
            Scene sc = label.getScene();
            Window win = sc.getWindow();
            label.getTooltip().setAutoHide(true);
            label.getTooltip().show(win, point.getX() + sc.getX() + win.getX(),
                    point.getY()  + sc.getY() + win.getY());
        }
    }

    /**
     * Creates a validation event handler. This handler will install an icon decorator to the node to be validated, and
     * also set the pseudo-class as "validation-ok", "validation-info", "validation-warning", "warning-error" so that
     * you can use css file to display the validation result.
     *
     * @param targetNode the node to be validated
     * @return the event handler
     */
    public static EventHandler<ValidationEvent> createDefaultValidationEventHandler(Node targetNode, TriFunction<Node, Decorator, ValidationEvent, Decorator<Label>> decorationCreator) {
        return new EventHandler<ValidationEvent>() {
            Decorator resultDecorator = null;

            @Override public void handle(ValidationEvent event) {
                if (event != null) {
                    adjustValidationPseudoClasses(event, targetNode);

                    if (event.getEventType().equals(ValidationEvent.VALIDATION_UNKNOWN)) {
                        DecorationUtils.uninstall(targetNode);
                        resultDecorator = null;
                        targetNode.getParent().requestLayout();
                        return;
                    }

                    if (targetNode instanceof Cell && event.getEventType().equals(ValidationEvent.VALIDATION_OK)) 
                        return;
                    

                    // check to make sure it is the same event type and even message
                    Object validationResultProperty = targetNode.getProperties().get(PROPERTY_VALIDATION_RESULT);
                    Object validationResultMessageProperty = targetNode.getProperties().get(PROPERTY_VALIDATION_RESULT_MESSAGE);
                    if (event.getEventType().getName().equals(validationResultProperty)
                        && event.getMessage().equals(validationResultMessageProperty)) 
                        return;
                    

                    // Create decorator using passed function (look into ValidationDecorators)
                    resultDecorator = decorationCreator.apply(targetNode, resultDecorator, event);
                    adjustValidationPseudoClasses(event, resultDecorator.getNode());

                    targetNode.getProperties().put(PROPERTY_VALIDATION_RESULT, event.getEventType().getName());
                    targetNode.getProperties().put(PROPERTY_VALIDATION_RESULT_MESSAGE, event.getMessage());
                }
            }
        };
    }

    private static void adjustValidationPseudoClasses(ValidationEvent event, Node targetNode) {

        boolean ok = event.getEventType() == ValidationEvent.VALIDATION_OK;
        boolean info =  event.getEventType() == ValidationEvent.VALIDATION_INFO;
        boolean warning =  event.getEventType() == ValidationEvent.VALIDATION_WARNING;
        boolean err =  event.getEventType() == ValidationEvent.VALIDATION_ERROR;
        targetNode.pseudoClassStateChanged(PseudoClass.getPseudoClass(PSEUDO_CLASS_VALIDATION_OK), ok);
        targetNode.pseudoClassStateChanged(PseudoClass.getPseudoClass(PSEUDO_CLASS_VALIDATION_INFO), info);
        targetNode.pseudoClassStateChanged(PseudoClass.getPseudoClass(PSEUDO_CLASS_VALIDATION_WARNING), warning);
        targetNode.pseudoClassStateChanged(PseudoClass.getPseudoClass(PSEUDO_CLASS_VALIDATION_ERROR), err);
    }

    public static ValidationStatus getValidationStatus(Node targetNode) {
        Object validationResult = targetNode.getProperties().get(PROPERTY_VALIDATION_RESULT);
        if (validationResult != null) 
        {
            String str = validationResult.toString();
            if (str.equals(ValidationEvent.VALIDATION_ERROR.getName()))     return ValidationStatus.VALIDATION_ERROR;
            if (str.equals(ValidationEvent.VALIDATION_INFO.getName()))      return ValidationStatus.VALIDATION_INFO;
            if (str.equals(ValidationEvent.VALIDATION_OK.getName()))        return ValidationStatus.VALIDATION_OK;
            if (str.equals(ValidationEvent.VALIDATION_UNKNOWN.getName()))   return ValidationStatus.VALIDATION_UNKNOWN;
            if (str.equals(ValidationEvent.VALIDATION_WARNING.getName()))   return ValidationStatus.VALIDATION_WARNING;
        }
        return ValidationStatus.VALIDATION_UNKNOWN;
    }
    

/**
 * {@code ValidationObject} is an object containing the information needed by a {@code Validator}. It has
 * three things - the source, the new value and old value.
 * <p>
 * The source is the object who has the Validator. It is usually the Node to be validated. For example, in the case of
 * validating a text field, the source will be the text field.
 * <p>
 * Normally {@code ValidationObject} is accompanied by the old and new value. If the value is a primitive type
 * (such as int or boolean) it must be wrapped as the corresponding java.lang.* Object type (such as Integer or
 * Boolean).
 * <p>
 * Null values may be provided for the old and the new values if their true values are not known. Please note, the new
 * value could be a different data type which were failed to be converted to the expected. For example, for an integer
 * field, the new value could be String if the value cannot be converted to an integer. A correctly?written
 * {@code Validator} should check for the data type of the new value and generate a proper ValidationEvent when the
 * new value has the wrong data type.
 * <p>
 * Users can extend this class to create their own {@code ValidationObject} to provide additional information that
 * are needed by {@code Validator}. For example, {@code TableValidationObject} extends
 * {@code ValidationObject} to add row and column information in a table.
 */
 class ValidationObject {
    
	private Object _source; // The source. It is usually the node to be validated.
    private Object _newValue;
    private Object _oldValue;				// Previous value. May be null if not known.

    public Object getSource() {        return _source;    }
    public Object getNewValue() {        return _newValue;    }
    public Object getOldValue() {        return _oldValue;    }

   /**
     * Constructs a new {@code ValidationObject}.
     *
     * @param source   The source that sends this ValidationObject.
     * @param oldValue The old value. Please note, the old value is optional, so it might be null in some cases.
     * @param newValue The new value.
     */
    public ValidationObject(Object source, Object oldValue, Object newValue) {
        _source = source;
        _newValue = newValue;
        _oldValue = oldValue;
    }

     @Override public String toString() {
        String properties = " [ source=" + getSource() + " oldValue=" + getOldValue() + " newValue=" + getNewValue() + " ]"; //NON-NLS
        return getClass().getName() + properties;
    }
}

 }

