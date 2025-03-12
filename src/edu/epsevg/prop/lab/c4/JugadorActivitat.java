package edu.epsevg.prop.lab.c4;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sergi i Ehsan
 */
public class JugadorActivitat implements Jugador, IAuto 
{
    private final int profunditat;
    private int colorJugadorActivitat;
    
    public JugadorActivitat(int profunditat)
    {
        this.profunditat = profunditat;
    }
    
    @Override
    public int moviment(Tauler t, int color) 
    {
        colorJugadorActivitat = color;
        
        int millorColumna = -1;
        int millorValue = Integer.MIN_VALUE;
        int alfa = Integer.MIN_VALUE;
        
        // Generar i ordenar els moviments possibles
        List<Integer> moviments = new ArrayList<>();
        for (int i = 0; i < t.getMida(); ++i)
        {
            if (t.movpossible(i))
            {
                moviments.add(i);
            }
        }

        moviments.sort((a, b) -> {
            Tauler tAuxA = new Tauler(t);
            Tauler tAuxB = new Tauler(t);
            tAuxA.afegeix(a, color);
            tAuxB.afegeix(b, color);
            
            int valueA = getHeuristica(tAuxA, color);
            int valueB = getHeuristica(tAuxB, color);
            
            return Integer.compare(valueB, valueA); // Ordre descendent
        });

        // Bucle per evaluar cada moviment
        for(int i : moviments) 
        {
            Tauler tAux = new Tauler(t);
            tAux.afegeix(i, color);

            int value = minimax(tAux, alfa, Integer.MAX_VALUE, profunditat - 1, -color, i);

            if (value > millorValue) {
                millorValue = value;
                millorColumna = i;
            }

            alfa = Math.max(alfa, millorValue);
        }
        return millorColumna;
        
    }

    @Override
    public String nom()
    {
        return "JugadorActivitat";
    }
    
    public int minimax(Tauler t, int alfa, int beta, int profunditat, int color, int moviment)
    {
        //if(t.solucio(moviment, color) || profunditat == 0) return getHeuristica(t, color);
        
        if(t.solucio(moviment, color) || profunditat == 0) return getHeuristica(t, color) - getHeuristica(t, -color);
        
        int v;
        
        if(color == colorJugadorActivitat)
        {
            v = Integer.MIN_VALUE;
            
            for(int i = 0; i < t.getMida(); ++i)
            {
                if(t.movpossible(i))
                {
                    Tauler tAux = new Tauler(t);
                    tAux.afegeix(i, color);
                    
                    if(t.solucio(i, color)) v = -1000;
                    
                    v = Math.max(v, minimax(tAux, alfa, beta, profunditat - 1, -color, i));

                    if(beta <= v) return v;

                    alfa = Math.max(v, alfa);
                }
            }
        }
        else
        {
            v = Integer.MAX_VALUE;

            for(int i = 0; i < t.getMida(); ++i)
            {   
                if(t.movpossible(i))
                {
                    Tauler tAux = new Tauler(t);
                    tAux.afegeix(i, color);
                    
                    if(t.solucio(i, -color)) v = 1000;
                    v = Math.min(v, minimax(tAux, alfa, beta, profunditat - 1, -color, i));

                    if(v <= alfa) return v;

                    beta = Math.min(v, beta);
                }
            }
        }
        
        return v;
    }
    
    int getHeuristica(Tauler t, int color)
    {
        int p = 0;
        
        int midaTauler = t.getMida();
        
        // Revisem les files
        for(int i = 0; i < midaTauler; ++i)
        {
            for(int j = 0; j<= (midaTauler - 4); ++j)
            {
                p += comptaFitxes(t, color, i, j, 0, 1);
            }
        }
        
        // Revisem les columnes
        for(int i = 0; i <= (midaTauler - 4); ++i)
        {
            for(int j = 0; j < midaTauler; ++j)
            {
                p += comptaFitxes(t, color, i, j, 1, 0);
            }
        }
        
        // Revisem diagonal \
        for(int i = 0; i < (midaTauler - 4); ++i)
        {
            for(int j = 0; j < (midaTauler - 4); ++j)
            {
                p += comptaFitxes(t, color, i, j, 1, 1);
            }
        }
        
        // Revisem diagonal /
        for(int i = 3; i < midaTauler; ++i)
        {
            for(int j = 0; j <= (midaTauler - 4); ++j)
            {
                p += comptaFitxes(t, color, i, j, -1, 1);
            }
        }
        return p;
    }
    
    public int comptaFitxes(Tauler t, int color, int f, int c, int df, int dc)
    {
        int fJugador = 0;
        int fOponent = 0;
        
        int midaTauler = t.getMida();
        
        for(int i = 0; i < 4; ++i)
        {
            int posX = f + i * df;
            int posY = c + i * dc;
            
            if(posX >= 0 && posX < midaTauler && posY >= 0 && posY < midaTauler)
            {
                int colorPos = t.getColor(posX, posY);
                if(colorPos == color)
                {
                    fJugador++;
                }
                else if(colorPos == -color)
                {
                    fOponent++;
                }
            }
        }
        
        if (fJugador > 0 && fOponent > 0) {
            return 0;
        }
        else if(fJugador > 0 && fOponent > 0)
        {
            return 0;
        }
        else if(fJugador == 4)
        {
            return 1000; // Conexió del jugador
        } 
        else if(fOponent == 4)
        {
            return -1000; // Conexió del oponent
        } 
        else if(fJugador == 3 && fOponent == 0)
        {
            return 250; // Bon potencial per completar una línia.
        } 
        else if(fOponent == 3 && fJugador == 0)
        {
            return -500; // Perill inminent del oponent.
        }
        else
        {
            return fJugador - fOponent; // Valor relatiu
        }
    }   
}