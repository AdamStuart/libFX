package util;

import java.net.URI;
import java.util.ArrayList;
import java.util.function.Supplier;

import org.apache.commons.validator.routines.DateValidator;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.TimeValidator;
import org.apache.commons.validator.routines.UrlValidator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import validation.Decorator;
import validation.SimpleValidator;
import validation.ValidationEvent;
import validation.ValidationObject;
import validation.ValidationUtils;
import validation.ValidationUtils.ValidationMode;
import validation.Validator;

public class FormsUtil
{
	public static VBox makeFormContainer()
	{
		VBox pane = new VBox();
		pane.setSpacing(12);
		pane.setPadding(new Insets(8));
		pane.setPrefHeight(500);
		pane.setPrefWidth(500);
		return pane;
	}

	public static HBox makeLabelFieldHBox(String prefix, String prompt, String id)
	{
		Label label = makePrompt(prompt, id);  label.setAlignment(Pos.CENTER_RIGHT);
		TextField field = new TextField();
		field.setId(prefix + id + "Field");
		return new HBox(4, label, field);
	}

	public static HBox makeLabelFieldHBox(String prompt, String id)	{ return makeLabelFieldHBox("", prompt, id); } 

	public static HBox makeLabelFieldHBox(String prefix, String prompt, String id, int labelWidth, int fldWidth)
	{
		Label label = makePrompt(prompt, id);
		label.setTextAlignment(TextAlignment.RIGHT);
		TextField field = new TextField();
		field.setId(prefix + id + "Field");
		if (fldWidth > 0) 	field.setPrefWidth(fldWidth);
		return new HBox(label, field);
	}

	public static HBox makeLabelNumberFieldHBox(String prompt, String id, int labelWidth, int fldWidth)
	{
		return makeLabelNumberFieldHBox("",  prompt,  id,  labelWidth, fldWidth);
	}

	public static HBox makeLabelNumberFieldHBox(String prefix, String prompt, String id, int labelWidth, int fldWidth)
	{
		Label label = makePrompt(prompt, id);
		label.setTextAlignment(TextAlignment.RIGHT);
		TextField field = new TextField();
		field.setId(prefix + id + "Field");
		field.setStyle(" -fx-alignment: CENTER-RIGHT;");
		if (fldWidth > 0) 	field.setPrefWidth(fldWidth);
		return new HBox(label, field);
	}

	public static HBox makeLabelFieldHBox(String prefix, String prompt, String id, int fldWidth)
	{
		Label label = makePrompt(prompt, id);
		label.setAlignment(Pos.CENTER_RIGHT);
		TextField field = new TextField();
		field.setId(prefix + id + "Field");
		if (fldWidth > 0) 	field.setPrefWidth(fldWidth);
		return new HBox(label, field);
	}
	public static HBox makeRightLabelFieldHBox(String prompt, String id, int fldWidth)
	{
		return makeRightLabelFieldHBox("", prompt, id, fldWidth);
	}
	public static HBox makeRightLabelFieldHBox(String prefix, String prompt, String id, int fldWidth)
	{
		Label label = makePrompt(prompt, id, 200);
//		label.setTextAlignment(TextAlignment.RIGHT);
		label.setAlignment(Pos.CENTER_RIGHT);
		TextField field = new TextField();
		field.setId(prefix + id + "Field");
		if (fldWidth > 0) 	field.setPrefWidth(fldWidth);
		return new HBox(label, field);
	}

	public static HBox makeURLBox()	{ return makeURLBox(""); } 
	public static HBox makeURLBox(String prefix)
	{
		Label label = makePrompt("URL");
		TextField field = new TextField();		field.setId(prefix + "url"); field.setPrefWidth(300);
	    Button urlButton = new Button("Open", new ImageView(new Image("/validation/web.png")));				// TODO path
	    urlButton.setId(prefix + "urlButton");
	    urlButton.setOnAction(new EventHandler<ActionEvent>() {
	            @Override  public void handle(ActionEvent event) {
	                try 
	                {
	                    java.awt.Desktop.getDesktop().browse(new URI(field.getText()));
	                }
	                catch (Exception e) {}  // ignore
	            }
	        });
	        urlButton.setDisable(true);
		
	        field.addEventHandler(ValidationEvent.ANY, new EventHandler<ValidationEvent>() {
	            @Override
	            public void handle(ValidationEvent event) {
	            	urlButton.setDisable(event.getEventType() != ValidationEvent.VALIDATION_OK);
	            }
	        });

//	        ValidationUtils.install(field, new SimpleValidator(UrlValidator.getInstance()), ValidationMode.ON_FLY);
		return new HBox(label, field, urlButton);
	}
	public static HBox makeEmailBox()	{  return makeEmailBox("");	}

	public static HBox makeEmailBox(String prefix)
	{
		Label label = makePrompt("Email");
		TextField field = new TextField();
		field.setId(prefix + "email");
//		ValidationUtils.install(field, new SimpleValidator(EmailValidator.getInstance()), ValidationMode.ON_FLY);
//        ValidationUtils.install(field, new Validator() {
//            @Override
//            public ValidationEvent call(ValidationUtils.ValidationObject param) 
//            {
//                if (field.getText().trim().length() == 0) 
//                    return new ValidationEvent(ValidationEvent.VALIDATION_ERROR, 0, "The email cannot be empty!");
//                 
//                return new SimpleValidator(EmailValidator.getInstance()).call(param);
//            }
//        }, ValidationMode.ON_DEMAND);
        Supplier<Decorator> requiredFactory = new Supplier<Decorator>() {
            @Override public Decorator get() {
            	Image i = new Image("/validation/overlay_required.png");
                return new Decorator<>(new ImageView(i));
            }
        };
        HBox box = new HBox(label, field);
        ValidationUtils.forceValidate(field, ValidationMode.ON_FLY);
		return box;
	}

	public static HBox makeDateBox(String prefix, boolean editable, int width)
	{
		Label label = makePrompt("Date");		label.setPrefWidth(width);
		DatePicker field = new DatePicker();
		field.setId(prefix + "date");
		field.setDisable(!editable);
//		ValidationUtils.install(field, new SimpleValidator(DateValidator.getInstance()), ValidationMode.ON_FLY);
		return new HBox(label, field);
	}
static int DATE_WIDTH = 100;
	public static HBox makeDateBox(String prefix, boolean editable)
	{
		Label label = makePrompt("Date");	
		DatePicker field = new DatePicker();
		field.setId(prefix + "date");
		field.setPrefWidth(DATE_WIDTH);
		field.setDisable(!editable);
		SimpleValidator checker =  new SimpleValidator(DateValidator.getInstance());
//		ValidationUtils.install(field,checker, ValidationMode.ON_FLY);
		return new HBox(label, field);
	}

	public static HBox makeTimeBox(String prefix, boolean editable)
	{
		Label label = makePrompt("Time");
		TextField field = new TextField();
		field.setId(prefix + "time");
		field.setDisable(!editable);
		field.setPrefWidth(DATE_WIDTH);
//		ValidationUtils.install(field, new SimpleValidator(TimeValidator.getInstance()), ValidationMode.ON_FLY);
		return new HBox(label, field);
	}

	public static HBox makeDurationBox(String prefix, boolean editable)
	{
		Label label = makePrompt("For");
		ObservableList list = FXCollections.observableArrayList();
		list.addAll("Minutes", "Hours", "Days");
		ChoiceBox<String> choice = new ChoiceBox<String>(list);
		choice.setId(prefix + "units");
		TextField field = new TextField();
		field.setPrefWidth(40 );
		field.setId(prefix + "duration");
		field.setDisable(!editable);
		return new HBox(label, field, choice);
	}

	
	
	public static HBox makeTimeDateDurationBox(String prefix, String prompt, boolean showTime, boolean showDate, boolean showDuration)
	{
		ArrayList<Node> kids = new ArrayList<Node>();
		Label label = makePrompt(prompt);
		kids.add(label);
		if (showTime)		kids.add(makeDateBox(prefix, true));
		if (showDate)		kids.add(makeTimeBox(prefix, true));
		if (showDuration)		kids.add(makeDurationBox(prefix, true));
		HBox h = new HBox();
		h.getChildren().addAll(kids);
		return h;
	}
	public static HBox makeNameHBox()	{ return makeNameHBox(""); } 

	public static HBox makeNameHBox(String prefix)
	{
		TextField field = new TextField();
		field.setId(prefix + "firstnameField");
		TextField lastField = new TextField();
		field.setId(prefix + "lastnameField");
		return new HBox(makePrompt("First", "firstname"), field, makePrompt("Last", "lastname"), lastField);
	}
	public static VBox makeAddressVBox(int prefWidth, boolean intl)	 { return makeAddressVBox("", prefWidth, intl);		}

	public static VBox makeAddressVBox(String prefix, int prefWidth, boolean intl)
	{
		TextField addr1 = new TextField();		addr1.setId(prefix + "addr1");
		TextField addr2 = new TextField();		addr2.setId(prefix + "addr2");
		TextField city = new TextField();		city.setId(prefix + "city");
		TextField st = new TextField();			st.setId(prefix + "st");
		TextField zip = new TextField();		zip.setId(prefix + "zip");

		addr1.setPrefWidth(prefWidth - 60);
		addr2.setPrefWidth(prefWidth);
		st.setPrefWidth(40);
		zip.setPrefWidth(100);

		HBox line1 = new HBox(makePrompt("Address", "addr1"), addr1);
		HBox line2 = new HBox(addr2);
		HBox line3 = new HBox(10, makePrompt("City", "city"), city, 
				makePrompt("State", "st"), st,
				makePrompt("Zip", "zip"), zip);
		
		VBox v = new VBox(line1, line2, line3);
		if (intl)
		{
			HBox line4 = new HBox(makePrompt("Country", "country"));
			v.getChildren().add(line4);
		}
		v.setSpacing(6);
		return v;
	}

	public static boolean addColon = true;

	public static Label makePrompt(String s, String id, int width)
	{
		String S = lookup(s);
		if (addColon)
			S += ": ";
		Label la = new Label(S);  
		if (id != null)			la.setId(id + "Label");
		if (width > 0 )			la.setPrefWidth(width);
		return la;
	}

	public static Label makePrompt(String s, String id)
	{
		String S = lookup(s);
		if (addColon)			S += ": ";
		Label la = new Label(S);
		if (id != null)
			la.setId(id + "Label");
		return la;
	}

	public static Label makePrompt(String s)	{		return makePrompt(s, null);	}
	public static String lookup(String s)	{		return s;	} // dummy lookup function supports later localization

	public static HBox formbox(String string, String id, int i)	{	return new HBox(makePrompt(string, id, i));	}

	public static HBox promptedText(String string, String string2, int i)
	{
		Label label = makePrompt(string, string2, 200);
//		label.setTextAlignment(TextAlignment.RIGHT);
		label.setAlignment(Pos.CENTER_RIGHT);
		TextField field = new TextField();
		field.setId(i + "Field");
//		if (fldWidth > 0) 	field.setPrefWidth(fldWidth);
		return new HBox(label, field);
	}

	
}
