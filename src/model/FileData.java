package model;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

// line format is "01/15/1959", "23", "3", "325", "32"
public class FileData {
	private List<TreeMap<LocalDateTime, Integer>> chartValues;
	private String fileName;
	private List<File> files;
	private AggregationLevel level;
	
	public List<TreeMap<LocalDateTime, Integer>> getChartValues() 			{	return chartValues;	}
	public void setChartValues(List<TreeMap<LocalDateTime, Integer>> values){	chartValues = values;	}

	public void addChartValues(int index, TreeMap<LocalDateTime, Integer>chartValue) {
		
		if(index<chartValues.size())
		{
			TreeMap<LocalDateTime, Integer> tempValue;
			tempValue = chartValues.remove(index);
			chartValue.putAll(tempValue);	
		}
		chartValues.add(index,chartValue);
	}

	public int findValFromString(String str)
	{
		int value;
		try
		{
			value = Integer.parseInt(str);
		} catch (NumberFormatException e) {
		try
		{
			value = (int) Double.parseDouble(str);
		} catch (NumberFormatException f) 		{ return 0;}
			return value;
		}
		return value;
	}

	/**
	 * @return the AggregationLevel
	 */
	public AggregationLevel getLevel() { return level; }
	public void setLevel(AggregationLevel l) {	level = l;	}
	/**
	 * @return the files
	 */
	public List<File> getFiles() 		{		return files;	}
	public void setFiles(List<File> fs) {		files = fs;	}

	public String getFileName() 		{		return fileName;	}
	public void setFileName(String fil) {		fileName = fil;	}

	public FileData() {
		chartValues = new ArrayList<TreeMap<LocalDateTime, Integer>>();//TreeMap<LocalDateTime, Integer>();
	}

	public String getString(int number) 
	{
		if (number < 10)	return "0" + String.valueOf(number);
		return String.valueOf(number);
	}
	//-------------------------------------------------------------------------------------
	public TreeMap<LocalDateTime, Integer> aggMinuteData(LocalDate date, String[] points) {
		int hourOfDay = 0;
		int minutes = 0;
		TreeMap<LocalDateTime, Integer> chartValue = new TreeMap<LocalDateTime, Integer>();
		
		for (int i = 1; i < 60 * 24; i++, minutes++) {
			if (minutes == 60) {	hourOfDay++;	minutes = 0; }
			int value;
			if (i < points.length) {
				value = findValFromString(points[i]);
				System.out.println(value);
			} 
			else value = 0;

			LocalTime time = LocalTime.MIDNIGHT;
			time = time.withHour(hourOfDay);
			time = time.withMinute(minutes);			
			chartValue.put(date.atTime(time), value);
		}
		return chartValue;
	}

	public TreeMap<LocalDateTime, Integer>  aggHourData(LocalDate date, String[] points) {
		int hourOfDay = 0;
		int minutes = 0;
		int value = 0;
		TreeMap<LocalDateTime, Integer> chartValue = new TreeMap<LocalDateTime, Integer>();
		for (int i = 1; i < 24 * 60; i++, minutes++) {
			if (minutes == 60) {
				hourOfDay++;
				LocalTime time = LocalTime.MIDNIGHT;
				time = time.withHour(hourOfDay);
				chartValue.put(date.atTime(time), value);
				minutes = 0;
				value = 0;
			}
			if  (i < points.length)
				value +=  findValFromString(points[i]);
		}
		return chartValue;
	}

	public TreeMap<LocalDateTime, Integer> aggDayData(LocalDate date, String[] points) {
		TreeMap<LocalDateTime, Integer> chartValue = new TreeMap<LocalDateTime, Integer>();
		int value = 0;
		for (int i = 1; i < points.length; i++) 
			value += findValFromString(points[i]);
		
		chartValue.put(date.atStartOfDay(), value);
		System.out.println(points[0] + ": " + value);
		return chartValue;
	}

	public TreeMap<LocalDateTime, Integer> aggWeekData(LocalDate date, String[] points) {
		TreeMap<LocalDateTime, Integer> chartValue = new TreeMap<LocalDateTime, Integer>();
		int value = 0;
		for (int i = 1; i < points.length; i++) 
			value += findValFromString(points[i]);
		
		int dayWeek = date.getDayOfWeek().getValue();
		LocalDateTime time = date.plusDays(8-dayWeek).atStartOfDay();
		if(chartValue.containsKey(time))
			value =chartValue.get(time)+value;
		chartValue.put(time, value);
		return chartValue;
	}
	
	public TreeMap<LocalDateTime, Integer> aggMonthData(LocalDate date, String[] points) {
		TreeMap<LocalDateTime, Integer> chartValue = new TreeMap<LocalDateTime, Integer>();
		int value = 0;
		for (int i = 1; i < points.length; i++) 
			value += findValFromString(points[i]);
		
		LocalDateTime time = date.withDayOfMonth(1).atStartOfDay();
		if(chartValue.containsKey(time))
			value =chartValue.get(time)+value;
		chartValue.put(time, value);
		return chartValue;
	}

	public TreeMap<LocalDateTime, Integer> aggYearData(LocalDate date, String[] points) {
		TreeMap<LocalDateTime, Integer> chartValue = new TreeMap<LocalDateTime, Integer>();
		int value = 0;
		for (int i = 1; i < points.length; i++) 
			value += findValFromString(points[i]);
		
		LocalDateTime time = date.withDayOfYear(1).atStartOfDay();
		if(chartValue.containsKey(time))
			value = chartValue.get(time)+value;
		chartValue.put(time, value);
		return chartValue;
	}
	//-----------------------------------------------------------------------------------------
	public void collectData(LocalDate fromDate, LocalDate endDate, boolean multiLine) {
		FileInputStream finStream = null;
		BufferedReader buffReader = null;
		chartValues.clear();
		int i = 0;
		for(File file:files)
		{
		try 
		{
			finStream = new FileInputStream(file);
			String line;
			String cvsSplitBy = ",";

			buffReader = new BufferedReader(new InputStreamReader(finStream));

			while ((line = buffReader.readLine()) != null) {
				String[] points = line.split(cvsSplitBy);
				LocalDate date;
				
				final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");		
				date = LocalDate.parse(points[0],dtf);
				Period difference = Period.between(fromDate, date);
				
				if(difference.isNegative())			continue;

				difference = Period.between(date, endDate);
				if(difference.isNegative())			continue;
				
				switch (level) {
				case MINUTES:	addChartValues(i,aggMinuteData(date, points));		break;
				case HOUR:		addChartValues(i,aggHourData(date, points));		break;
				case DAY:		addChartValues(i,aggDayData(date, points));			break;
				case WEEK:		addChartValues(i,aggWeekData(date, points));		break;
				case MONTH:		addChartValues(i,aggMonthData(date, points));		break;
				case YEAR:		addChartValues(i,aggYearData(date, points));		break;

				default:		break;			
				}
			}

		} catch (FileNotFoundException e) {			e.printStackTrace();			
		} catch (IOException e) {			e.printStackTrace();		} 
		finally 
		{
			try {
				finStream.close();
				buffReader.close();
			} catch (IOException e) {		e.printStackTrace();	} 
		}
		if(multiLine)
			i++;
		}
	}

}
