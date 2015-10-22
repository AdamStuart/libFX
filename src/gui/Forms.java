package gui;

import java.util.ArrayList;

import icon.FontAwesomeIcons;
import icon.GlyphsDude;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import util.StringUtil;

/*
 * FormsUtil contains a lot of static functions that will make
 * input boxes for special types of fields.  
 * Validation is done based on listening to the fields text changes.
 */

public class Forms
{
	public enum ValidationType 		{   NONE, STRING, WHOLE, INT,	DOUBLE,	 DATE,	PERCENT, CURRENCY, URL, EMAIL, 
										ISBN, DOTTED3, IP4, IP6, CREDITCARD, ZIP, PHONE	}		//AST

	public enum ValidationState 	{   OK, WARNING, REQUIRED, ERROR,	LOCKED,	UNKNOWN	}

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
		String fldId = prefix + id + "Field";
		field.setId(fldId);
		field.getStylesheets().add(fldId);
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
		field.getStylesheets().add(prefix + id + "Field");
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
	//--------------------------------------------------------------------------------------------
	public static HBox makeFormField(String prompt, String id, int fldWidth, String tooltip)
	{
		Label label = Forms.makePrompt(prompt, id);
//		label.setTextAlignment(TextAlignment.RIGHT);
		label.setAlignment(Pos.BOTTOM_RIGHT);
		TextField field = new TextField();
		Tooltip.install(field, new Tooltip(tooltip));
		Tooltip.install(label, new Tooltip(tooltip));
		field.setId(id);
		if (fldWidth > 0) 	field.setPrefWidth(fldWidth);
		return new HBox(label, field);
	}
//-----------------------------------------------------------------------------------------------------
	public static HBox makeURLBox()					{ return makeURLBox("", "URL", 0, 200, "Internet Resource Location"); } 
	public static HBox makeURLBox(String prefix)	{return makeURLBox(prefix, "URL", 0, 200, null);	}
	
	public static HBox makeURLBox(String prefix, String prompt, int labelwidth, int width, String tip )
	{
		HBox box = new HBox();
	    Button urlButton = new Button("Open");				// TODO path  , new ImageView(new Image("/validation/web.png")
	    urlButton.setGraphic((GlyphsDude.createIcon(FontAwesomeIcons.GLOBE)));
	    urlButton.setId(prefix + "urlButton");
	    urlButton.setDisable(true);

	    Label label = makePrompt(prompt, "", labelwidth);
		label.setAlignment(Pos.BOTTOM_RIGHT);
		TextField field = new TextField();		
		field.setId(prefix + "url"); 
		if (width > 0) field.setPrefWidth(width);
	    urlButton.setOnAction(event-> { StringUtil.launchURL(field.getText()); });
		field.textProperty().addListener((obs, old, newval) -> {
			if (newval == null || newval.equals(old)) return;
//			Button btn = (Button) box.lookup("" + prefix + "urlButton");
			boolean okay = StringUtil.isValidUrl(newval);
			urlButton.setDisable(!okay);	
		});

        if (tip!=null)
		{
			Tooltip ttip = new Tooltip(tip);
			Tooltip.install(label, ttip);
			Tooltip.install(field, ttip);
		}

	    box.getChildren().addAll(label, field, urlButton);
		return box;
	}

	//-----------------------------------------------------------------------------------------------------
	private static final String AMAZON_SERVICE = "http://www.amazon.com/s/ref=nb_sb_noss?url=search-alias%3Dstripbooks&field-keywords=";

	public static Node makeISBNBox(String prefix)
	{
        Label ISBNLabel = makePrompt("ISBN", "isbnLabel", 200);
        ISBNLabel.setAlignment(Pos.BOTTOM_RIGHT);
       ISBNLabel.setId(prefix + "ISBNLabel");
        TextField ISBNField = new TextField("");
        ISBNField.setId(prefix + "ISBNField");
		Label decoration = new Label("");
        Button amazonButton = new Button("Amazon");
        amazonButton.setGraphic((GlyphsDude.createIcon(FontAwesomeIcons.AMAZON)));
        amazonButton.setId(prefix + "amazonButton");
        amazonButton.setOnAction(event ->{ StringUtil.launchURL(AMAZON_SERVICE +  ISBNField.getText());   });
        amazonButton.setDisable(true);
        ISBNField.textProperty().addListener((obs, old, newval) -> {
			if (newval == null || newval.equals(old)) return;
			ValidationState validationState = StringUtil.validate(newval, ValidationType.ISBN, true );
			ISBNField.setBorder(Borders.getValidationBorder(validationState));  
			decoration.setGraphic(GlyphsDude.createValidationIcon(validationState));  
		});
       return new HBox(ISBNLabel,ISBNField, decoration, amazonButton);
	}	
	
	//-----------------------------------------------------------------------------------------------------
	public static HBox makeEmailBox()	{  return makeEmailBox("", "", 0, false);	}
	public static HBox makeEmailBox(String prefix, String id, int promptWidth, boolean required)
	{
		Label label = makePrompt("Email", "", promptWidth);
		label.setAlignment(Pos.BOTTOM_RIGHT);
		Label decoration = new Label("");
		TextField field = new TextField();
		field.setId(prefix + "email");

		field.textProperty().addListener((obs, old, newval) -> {
			if (newval == null || newval.equals(old)) return;
			ValidationState validationState = StringUtil.validate(newval, ValidationType.EMAIL, required );
			field.setBorder(Borders.getValidationBorder(validationState));  
			decoration.setGraphic(GlyphsDude.createValidationIcon(validationState));  
		});
		ValidationState initState = required ? ValidationState.REQUIRED : ValidationState.OK;
		field.setBorder(Borders.getValidationBorder( initState));  
		decoration.setGraphic(GlyphsDude.createValidationIcon(initState));  
        HBox box = new HBox(label, field, decoration);
//        ValidationUtils.forceValidate(field, ValidationMode.ON_FLY);
		return box;
	}
	//-----------------------------------------------------------------------------------------------------
	public static HBox makeDateBox( boolean editable, String toolTip)
	{
		return makeDateBox("Date", editable, 0, toolTip);
	}
	
	public static HBox makeDateBox(String prefix, boolean editable, int width)
	{
		return makeDateBox(prefix, editable, width, "");
	}
	
	public static HBox makeDateBox(String prefix, boolean editable, int width, String tip)
	{
		Label label = makePrompt("Date");		label.setPrefWidth(width);
		label.setAlignment(Pos.BOTTOM_RIGHT);
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
		label.setAlignment(Pos.BOTTOM_RIGHT);
		DatePicker field = new DatePicker();
		field.setId(prefix + "date");
		field.setPrefWidth(DATE_WIDTH);
		field.setDisable(!editable);
//		SimpleValidator checker =  new SimpleValidator(DateValidator.getInstance());
//		ValidationUtils.install(field,checker, ValidationMode.ON_FLY);
		return new HBox(label, field);
	}

	//-----------------------------------------------------------------------------------------------------
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

	//-----------------------------------------------------------------------------------------------------
	public static HBox makeDurationBox(String prefix, boolean editable)
	{
		Label label = makePrompt("For");
		ObservableList<String> list = FXCollections.observableArrayList();
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
	//-----------------------------------------------------------------------------------------------------
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

	//-----------------------------------------------------------------------------------------------------
	public static boolean addColon = true;

	public static Label makePrompt(String s, String id, int width)
	{
		String S = translate(s);
		if (addColon)
			S += ": ";
		Label la = new Label(S);  
		if (id != null)			la.setId(id + "Label");
		if (width > 0 )			la.setPrefWidth(width);
		return la;
	}

	public static Label makePrompt(String s, String id)
	{
		String S = translate(s);
		if (addColon)			S += ": ";
		Label la = new Label(S);
		if (id != null)
			la.setId(id + "Label");
		return la;
	}

	public static Label makePrompt(String s)	{		return makePrompt(s, null);	}
	public static String translate(String s)	{		return s;	} // dummy lookup function supports later localization

	public static HBox formbox(String string, String id, int i)	{	return new HBox(makePrompt(string, id, i));	}

	//-----------------------------------------------------------------------------------------------------
	public static HBox promptedText(String string, String string2, int i)
	{
		return promptedText(string, string2, i, "");
	}
	public static HBox promptedText(String string, String string2, int i, String tip)
	{
		Label label = makePrompt(string, string2, 200);
//		label.setTextAlignment(TextAlignment.RIGHT);
		label.setAlignment(Pos.CENTER_RIGHT);
		TextField field = new TextField();
		field.setId(i + "Field");
		field.setTooltip(new Tooltip(tip));
//		if (fldWidth > 0) 	field.setPrefWidth(fldWidth);
		return new HBox(label, field);
	}

	//--------------------------------------------------------------------------------------------
	public static HBox makeValidatedBox(String prompt, String id, ValidationType validationType, boolean required)
	{
		return makeValidatedBox(prompt, id, validationType, required, 0, 0);
	}
	
	public static HBox makeValidatedBox(String prompt, String id, ValidationType validationType, boolean required, int labelWidth, int fldWidth)
	{
		Label label = makePrompt(prompt, id, labelWidth);
		Label decoration = new Label("");
		decoration.setId(id + "ValidationIcon");
		label.setAlignment(Pos.BOTTOM_RIGHT);
		TextField field = new TextField();
		if (fldWidth > 0)
			field.setPrefWidth(fldWidth);
		String tip = getTooltipText(validationType);
		Tooltip.install(field, new Tooltip(tip));
		Tooltip.install(label, new Tooltip(tip));
		field.setId(id );
		if (validationType != ValidationType.NONE)
		{
			field.setOnKeyTyped(ev -> {		if (!StringUtil.isKeyLegal(ev, field, validationType))	ev.consume();	});		
			field.textProperty().addListener((obs, old, newval) -> {
				if (newval == null || newval.equals(old)) return;
				ValidationState validationState = StringUtil.validate(newval, validationType, required );
				field.setBorder(Borders.getValidationBorder(validationState));  
				decoration.setGraphic(GlyphsDude.createValidationIcon(validationState));  
			});
		}
		decoration.setGraphic(required ? GlyphsDude.createValidationIcon(ValidationState.REQUIRED) : null);  
		field.setBorder(Borders.getValidationBorder(required ? ValidationState.REQUIRED : ValidationState.OK));
		return new HBox(label, field, decoration);
	}


	public static String getTooltipText(ValidationType validationType)
	{
		if (validationType == ValidationType.DOUBLE)	return "Must be a decimal number";
		if (validationType == ValidationType.DATE)		return "Must be a valid date";
		if (validationType == ValidationType.INT)		return "Must be an integer";
		if (validationType == ValidationType.CURRENCY)	return "Must be a valid currency";
		if (validationType == ValidationType.PERCENT)	return "Must be a percentage between 0-100%";
		if (validationType == ValidationType.ISBN)		return "Must be a valid ISBN";
		if (validationType == ValidationType.ZIP)		return "Must be a valid 5 or 9 digit zipcode";
		if (validationType == ValidationType.PHONE)		return "Must be a ten digit phone number";
		return "";
	}
	//--------------------------------------------------------------------------------------------
	public static HBox makeRegulatoryStatusChoiceBox(boolean editable, String toolTip)
	{
		Label label = makePrompt("Regulatory Status", "reg", 200);
		label.setAlignment(Pos.BOTTOM_RIGHT);
		ObservableList<String> list = FXCollections.observableArrayList();
		list.addAll("Under Development", "For Discussion Only", "Alpha Test", "Beta Test", "Research Use Only", "In Clinical Trial", "In Medical Use");
		ChoiceBox<String> choice = new ChoiceBox<String>(list);
		choice.setId("regStatus");
		return new HBox(4, label, choice);
	}

	//--------------------------------------------------------------------------------------------
	public static HBox makeSchemaStatusBox(String string, String toolTip) {
		Label label = makePrompt("Schema Status", "schema", 200);
		label.setAlignment(Pos.BOTTOM_RIGHT);
		ObservableList<String> list = FXCollections.observableArrayList();
		list.addAll("Draft", "Official", "Alpha Test", "Copy of Official", "Modified");
		ChoiceBox<String> choice = new ChoiceBox<String>(list);
		choice.setId("status");
		return new HBox(4, label, choice);
	}

	// ----------------------------------------------------
	 public static Region createMultipleInstanceForm(String prefix)
	{
		 VBox container = Forms.makeFormContainer();
		 Region desc = Forms.makeMultipleInstanceBox("Description", "1");   // ("Project", "project", 400, tooltip);
		 Region reasearcher = Forms.makeMultipleInstanceBox("Author", "researcher");
		 container.getChildren().addAll(desc, reasearcher);
		 return container;
	}
	//--------------------------------------------------------------------------------------------
	public static VBox makeMultipleInstanceBox(String prompt, String id)
	{
		VBox lines = new VBox(4);
		addInstance(lines, prompt, id);
		return lines; 
	}
	
	private static int MAX_OCCURS= 4;
	public static void addInstance(VBox lines, String prompt, String id)
	{
		HBox line = makeLabelFieldHBox(prompt, id);
		Button plusButton = new Button("+");
		plusButton.setOnAction( event -> {     	addInstance(lines, prompt, id+"+"); 	plusButton.setVisible(false);	});				
		if (lines.getChildren().size() < MAX_OCCURS-1)
			line.getChildren().add(plusButton);
		lines.getChildren().add(line);		
	}
//	
 

}
