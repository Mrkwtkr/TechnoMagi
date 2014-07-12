package com.ollieread.technomagi.common.init;

import org.apache.logging.log4j.Level;

import com.ollieread.ennds.EnndsRegistry;
import com.ollieread.ennds.ISpecialisation;
import com.ollieread.ennds.Specialisation;
import com.ollieread.technomagi.TechnoMagi;

public class Specialisations
{

    public static ISpecialisation specScholar;
    public static ISpecialisation specMedic;
    public static ISpecialisation specWarrior;

    public static void init()
    {
        TechnoMagi.logger.log(Level.INFO, "Initiating & registering specialisations");

        specScholar = new Specialisation("scholar");
        specMedic = new Specialisation("medic");
        specWarrior = new Specialisation("warrior");

        EnndsRegistry.registerSpecialisation(specScholar);
        EnndsRegistry.registerSpecialisation(specMedic);
        EnndsRegistry.registerSpecialisation(specWarrior);
    }

}
