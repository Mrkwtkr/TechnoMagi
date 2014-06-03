package com.ollieread.technomagi.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.ollieread.technomagi.api.ability.IAbilityActive;
import com.ollieread.technomagi.api.ability.IAbilityPassive;
import com.ollieread.technomagi.api.research.IKnowledge;
import com.ollieread.technomagi.api.research.IResearch;
import com.ollieread.technomagi.player.PlayerKnowledge;

import cpw.mods.fml.common.eventhandler.Event;

/**
 * The registry for all things related to the mod. Allows for registration of
 * abilities, knowledge and research.
 * 
 * @author ollieread
 */
public class TMRegistry
{

    public static int ABILITY_PASSIVE = 0;
    public static int ABILITY_ACTIVE = 1;

    public static int EVENT_NONE = 0;
    public static int EVENT_SPECIALISATION = 1;
    public static int EVENT_RESEARCH_PROGRESS = 2;
    public static int EVENT_IN_FIRE = 3;
    public static int EVENT_PLAYER_TICK = 4;

    protected static List<ISpecialisation> specList = new ArrayList<ISpecialisation>();
    protected static List<IResearch> researchList = new ArrayList<IResearch>();
    protected static List<IAbilityPassive> passiveAbilityList = new ArrayList<IAbilityPassive>();
    protected static List<IAbilityActive> activeAbilityList = new ArrayList<IAbilityActive>();
    protected static List<IKnowledge> knowledgeList = new ArrayList<IKnowledge>();

    protected static HashMap<String, Integer> specNames = new HashMap<String, Integer>();
    protected static HashMap<String, Integer> researchNames = new HashMap<String, Integer>();
    protected static HashMap<String, Integer> passiveNames = new HashMap<String, Integer>();
    protected static HashMap<String, Integer> activeNames = new HashMap<String, Integer>();
    protected static HashMap<String, Integer> knowledgeNames = new HashMap<String, Integer>();

    protected static HashMap<Integer, List<Integer>> researchEvents = new HashMap<Integer, List<Integer>>();
    protected static HashMap<Integer, List<Integer>> passiveAbilityEvents = new HashMap<Integer, List<Integer>>();

    /*
     * protected static HashMap<String, ISpecialisation> specList = new
     * HashMap<String, ISpecialisation>(); protected static HashMap<String,
     * IResearch> researchList = new HashMap<String, IResearch>(); protected
     * static HashMap<String, IAbilityPassive> passiveAbilityList = new
     * HashMap<String, IAbilityPassive>(); protected static HashMap<String,
     * IAbilityActive> activeAbilityList = new HashMap<String,
     * IAbilityActive>(); protected static HashMap<String, IKnowledge>
     * knowledgeList = new HashMap<String, IKnowledge>();
     */

    public static void registerSpecialisation(ISpecialisation spec)
    {
        String name = spec.getName();

        if (getSpecialisation(name) == null && !specList.contains(spec)) {
            int i = specList.size();

            specList.add(i, spec);
            specNames.put(name, i);
        }
    }

    public static ISpecialisation getSpecialisation(String name)
    {
        int id = getSpecialisationId(name);

        if (id > -1) {
            return specList.get(id);
        }

        return null;
    }

    public static int getSpecialisationId(String name)
    {
        if (name.equals("none")) {
            return -1;
        } else if (specNames.containsKey(name)) {
            return specNames.get(name);
        }

        return -1;
    }

    public static String getSpecialisationName(int id)
    {
        return id > -1 ? specList.get(id).getName() : "none";
    }

    public static Collection<ISpecialisation> getSpecialisations()
    {
        return specList;
    }

    public static void registerKnowledge(IKnowledge knowledge)
    {
        String name = knowledge.getName();

        if (getKnowledge(name) == null && !knowledgeList.contains(knowledge)) {
            int i = knowledgeList.size();

            knowledgeList.add(i, knowledge);
            knowledgeNames.put(name, i);
        }
    }

    public static IKnowledge getKnowledge(String knowledge)
    {
        int id = getKnowledgeId(knowledge);

        if (id > -1) {
            return knowledgeList.get(id);
        }

        return null;
    }

    public static int getKnowledgeId(String name)
    {
        if (knowledgeNames.containsKey(name)) {
            return knowledgeNames.get(name);
        }

        return -1;
    }

    public static String getKnowledgeName(int id)
    {
        return id > -1 ? knowledgeList.get(id).getName() : null;
    }

    public static void registerResearch(IResearch research)
    {
        String name = research.getName();

        if (getResearch(name) == null && !researchList.contains(research)) {
            int i = researchList.size();
            researchList.add(i, research);
            researchNames.put(name, i);

            if (researchEvents.containsKey(research.getEvent())) {
                researchEvents.get(research.getEvent()).add(i);
            } else {
                researchEvents.put(research.getEvent(), Arrays.asList(i));
            }
        }
    }

    public static IResearch getResearch(String name)
    {
        int id = getResearchId(name);

        if (id > -1) {
            return researchList.get(id);
        }

        return null;
    }

    public static int getResearchId(String name)
    {
        if (researchNames.containsKey(name)) {
            return researchNames.get(name);
        }

        return -1;
    }

    public static String getResearchName(int id)
    {
        return id > -1 ? researchList.get(id).getName() : null;
    }

    public static void researchEvent(int id, Event event, PlayerKnowledge charon)
    {
        if (!charon.canSpecialise() && researchEvents.containsKey(id)) {
            for (Iterator<Integer> i = researchEvents.get(id).iterator(); i.hasNext();) {
                IResearch research = researchList.get(i.next());

                if (research.canPerform(charon)) {
                    charon.researchKnowledge(research.getName(), research.getKnowledge(), research.getProgress(), research.isRepeating());
                }
            }
        }
    }

    public static void registerActiveAbility(IAbilityActive ability)
    {
        String name = ability.getName();

        if (!activeAbilityList.contains(ability)) {
            int i = activeAbilityList.size();

            activeAbilityList.add(i, ability);
            activeNames.put(name, i);
        }
    }

    public static void registerPassiveAbility(IAbilityPassive ability)
    {
        String name = ability.getName();
        if (!passiveAbilityList.contains(ability)) {
            int i = passiveAbilityList.size();

            passiveAbilityList.add(i, ability);
            passiveNames.put(name, i);

            if (passiveAbilityEvents.containsKey(ability.getEvent())) {
                passiveAbilityEvents.get(ability.getEvent()).add(i);
            } else {
                passiveAbilityEvents.put(ability.getEvent(), Arrays.asList(i));
            }
        }
    }

    public static int getActiveAbilityId(String name)
    {
        if (activeNames.containsKey(name)) {
            return activeNames.get(name);
        }

        return -1;
    }

    public static int getPassiveAbilityId(String name)
    {
        if (passiveNames.containsKey(name)) {
            return passiveNames.get(name);
        }

        return -1;
    }

    public static String getPassiveAbilityName(IAbilityPassive ability)
    {
        Set<Entry<String, Integer>> set = passiveNames.entrySet();
        int id = passiveAbilityList.indexOf(ability);

        for (Iterator<Entry<String, Integer>> i = set.iterator(); i.hasNext();) {
            Entry<String, Integer> p = i.next();

            if (p.getValue().equals(i)) {
                return p.getKey();
            }
        }

        return null;
    }

    public static IAbilityActive getActiveAbility(String name)
    {
        int id = getActiveAbilityId(name);

        if (id > -1) {
            return activeAbilityList.get(id);
        }

        return null;
    }

    public static IAbilityPassive getPassiveAbility(String name)
    {
        int id = getPassiveAbilityId(name);

        if (id > -1) {
            return passiveAbilityList.get(id);
        }

        return null;
    }

    public static Collection<IAbilityActive> getActiveAbilityList()
    {
        return activeAbilityList;
    }

    public static Collection<IAbilityPassive> getPassiveAbilityList()
    {
        return passiveAbilityList;
    }

    public static void passiveAbilityEvent(int id, Event event, PlayerKnowledge charon)
    {
        if (!charon.canSpecialise() && passiveAbilityEvents.containsKey(id)) {
            for (Iterator<Integer> i = passiveAbilityEvents.get(id).iterator(); i.hasNext();) {
                IAbilityPassive ability = passiveAbilityList.get(i.next());

                if (charon.abilities.hasPassiveAbility(ability.getName())) {
                    ability.use(event, charon);
                }
            }
        }
    }

}
