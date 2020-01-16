package ru.es.audio.basics;

// ##################################################
// ############# Работа с децибеллами ###############
// ##################################################
public enum NormalizingPow
{
    Pow2(2, 0.5),
    NoPow(1, 1),
    SynthPow(3, 1.0/3.0);

    public double pow;
    public double revertPow = 1.0;

    NormalizingPow(double pow, double revertPow)
    {
        this.pow = pow;
        this.revertPow = revertPow;
    }
}
