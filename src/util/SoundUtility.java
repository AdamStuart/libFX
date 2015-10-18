package util;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

public class SoundUtility
{
	// Soundutility.playSound("click");		should be all you need
    public static void playSound(String soundName)
    {
        AudioClip audioClip = getClip(soundName);
        if (audioClip != null)
             audioClip.play();
     }
    //----------------------------------------------------------------------    
    private static URL codeBase;
    private static HashMap<URL, AudioClip> soundMap = new HashMap<URL, AudioClip>();

    static
    {
        try 
        {
            codeBase = new URL("file:" + System.getProperty("user.dir") + "/");
        } 
        catch (MalformedURLException e)         {    System.err.println(e.getMessage());    }        
    }
    
    //----------------------------------------------------------------------    
   // asynchronous method will return when the sound starts.
     
    private static AudioClip getClip(String soundName)
    {
        String extension = "wav";
        String soundDir = "sounds";
        String relativePath = soundDir + "/" + soundName + "." + extension;
        
        AudioClip audioClip = null;
        URL completeURL;
        try 
        {              
            completeURL = new URL(codeBase, relativePath);
            audioClip = soundMap.get(completeURL);
            if (audioClip == null)
            {
                URL jarURL = ClassLoader.getSystemResource(relativePath);
                if (jarURL != null)
                    audioClip = Applet.newAudioClip(jarURL);  
                else
                    audioClip = Applet.newAudioClip(completeURL);               
                if (audioClip != null)
                    soundMap.put(completeURL, audioClip);  
                else
                    System.err.println("Missing sound file: " + soundName + ".  Could not find at: " + jarURL + ", or: " + completeURL);
            }
        } 
        catch (MalformedURLException e)        {       System.err.println(e.getMessage());      }            
        return audioClip;
    }

    //----------------------------------------------------------------------    
      public static void mididemo() 
      {
          int[] notes = new int[]{60, 62, 64, 65, 67, 69, 71, 72, 72, 71, 69, 67, 65, 64, 62, 60};
          try {
              Synthesizer synthesizer = MidiSystem.getSynthesizer();
              synthesizer.open();
              MidiChannel channel = synthesizer.getChannels()[0];

              for (int note : notes) 
              {
                  channel.noteOn(note, 50);
                  try 
                  {
                      Thread.sleep(200);
                  } catch (InterruptedException e) {    break;} 
                  finally {       channel.noteOff(note);      }
              }
          } catch (MidiUnavailableException e) {         e.printStackTrace();       }
      }
    }
