package ru.es.audio.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;


public class MidiNote
{
    int note;  // нота (от 0 до 127) нота 60 - центральная нота ДО
    int velo;  // чувствительность (сила нажатия по клавишам) - от 0 до 127
    boolean on; // начало ноты или конец ноты. Начало - запускает проигрывание, конец - останавливает.
    public long position = 0;
    public int sampleOffset = 0;

    public MidiNote(int note, int velo, boolean on)
    {
        this.velo = velo;
        this.note = note;
        this.on = on;

        if (velo == 0) // по старому протоколу если velo == 0, значит это конец ноты
            this.on = false;
        if (!on)
            this.velo = 0;
    }

    public MidiNote(int note, int velo, boolean on, long position)
    {
        this(note, velo, on);
        this.position = position;
    }

    public int getNote()
    {
        return note;
    }

    public void setNote(int note)
    {
        this.note = note;
    }

    public int getVelo()
    {
        return velo;
    }

    public void setVelo(int velo)
    {
        this.velo = velo;
    }

    public boolean isOn()
    {
        return on;
    }

    public void setOn(boolean on)
    {
        this.on = on;
    }

    // конвертирование ноты. Именно такой метод конвертации используется для отправки нот на внешние устройства.
    public byte[] convertToByteArray(int channel)
    {
        byte firstHalf = (byte) ShortMessage.NOTE_ON;
        if (!isOn())
            firstHalf = (byte) ShortMessage.NOTE_OFF;


        byte note = (byte) (getNote());
        byte status = (byte) (firstHalf | channel);
        byte velo = (byte) getVelo();

        try
        {
            ShortMessage message = new ShortMessage(status, note, velo);
            return message.getMessage();
        }
        catch (InvalidMidiDataException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public ShortMessage convertToShortMessage(int tranpose, int midiChannel) throws InvalidMidiDataException
    {
        byte firstHalf = (byte) ShortMessage.NOTE_ON;
        if (!isOn())
            firstHalf = (byte) ShortMessage.NOTE_OFF;

        byte note = (byte) (getNote() + tranpose);

        byte status = (byte) (firstHalf | midiChannel);
        byte velo = (byte) getVelo();

        return new ShortMessage(status, note, velo);
    }
}
