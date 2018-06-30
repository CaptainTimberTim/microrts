/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EINT_Agent1;

import ai.evaluation.EvaluationFunction;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.Unit;

/**
 *
 * @author Tim Dierks
 */
public class AgentEvaluationFunction extends EvaluationFunction
{
    static float GameOverBonus               = 100000.0f;
    static float ResourceMultiplier          = 20.0f;
    static float ResourcesInWorkerMultiplier = 10.0f;
    static float NewUnitImportanceMultiplier = 1000.0f;
    static float UnitHPMultiplier            = 40.0f;
    static float DistanceMultiplier          = 1200.0f;
    static float EnemyUnityCountMultiplier   = 15000.0f;
    
    @Override
    public float evaluate(int MaxPlayer, int MinPlayer, GameState GS) 
    {
        float Result = 0.0f;
        float Score1 = CalculateScore(MaxPlayer, GS);        
        //float Score2 = CalculateScore(MinPlayer, GS);
        
        /*if(Score1 + Score2 == 0.0f)
        {
            Result = 0.5f;
        }
        else
        {
            Result = (2*Score1/(Score1 + Score2)) - 1;
        }*/
        return Score1;
    }
    
    private float CalculateScore(int Player, GameState GS)
    {
        System.out.println("Player: "+Player);
        float Result = GS.getPlayer(Player).getResources()*ResourceMultiplier;
        PhysicalGameState PhysicalGS = GS.getPhysicalGameState();
        boolean AnyUnit = false;
        int UnitCount = 0;
        int EnemyUnitCount = 0;
        for(Unit U : PhysicalGS.getUnits())
        {
            if(U.getPlayer() == Player && !U.getType().name.equals("Resource"))
            {
                UnitCount++;
            }
            else if(!U.getType().name.equals("Resource"))
            {
                EnemyUnitCount++;
            }
        }
        //float NewUnitImportance = (1.0f/UnitCount)*NewUnitImportanceMultiplier;
        for(Unit U : PhysicalGS.getUnits())
        {
            if(U.getPlayer() == Player)
            {
                if(!AnyUnit) AnyUnit = true;
                float DistanceBonus = 0;
                boolean GameWon = false;
                if(!U.getType().name.equals("Base"))
                {
                    Unit NU = null;
                    float SmallestDistance = 10000;
                    GameWon = true;
                    for(Unit U2 : PhysicalGS.getUnits())
                    {
                        if(U2.getPlayer() != Player && !U2.getType().name.equals("Resource"))
                        {
                            GameWon = false;
                            //float Dist = 1.0f/(float)Math.abs(Pos - U2.getPosition(PhysicalGS)) * DistanceMultiplier;
                            int UX = U.getX();
                            int UY = U.getY();
                            int U2X = U2.getX();
                            int U2Y = U2.getY();
                            float NewDist = Math.abs(UX - U2X) + Math.abs(UY - U2Y);
                            if(NewDist < SmallestDistance)
                            {
                                SmallestDistance = NewDist;
                                NU = U2;
                            }
                            System.out.println("\tType: "+U.getType().name+U.getID()+", POS: ("+UX+"/"+UY+")"+", "+"Dist: "+NewDist+", ENEMY "+U2.getType().name+": "+U2.getPosition(PhysicalGS)+"("+U2X+"/"+U2Y+")");
                        }
                    }
                    if(SmallestDistance != 10000)
                    {
                        DistanceBonus = (1.0f/SmallestDistance)*DistanceMultiplier;
                        System.out.println("ActualDistanceBonus: "+DistanceBonus + ", From: "+NU+", d: "+SmallestDistance);
                    }
                }
                float ResourceBonus = U.getResources()*ResourcesInWorkerMultiplier;//*NewUnitImportance;
                float HitpointBonus = (float) (U.getCost()*Math.sqrt(U.getHitPoints()/U.getMaxHitPoints())*UnitHPMultiplier);
                if(GameWon) 
                {
                    Result += GameOverBonus;
                }
                Result += DistanceBonus + ResourceBonus + HitpointBonus;
            }            
        }
        Result += (1.0f/EnemyUnitCount) * EnemyUnityCountMultiplier;
        if(!AnyUnit) Result = 0.0f;
        System.out.println("Result: "+Result);
        return Result;
    }

    @Override
    public float upperBound(GameState gs) {
        return 1.0f;
    }
    
}
