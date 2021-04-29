package data.scripts.campaign.submarkets;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.Script;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.CoreUIAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.submarkets.BaseSubmarketPlugin;
import com.fs.starfarer.api.util.Highlights;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import java.util.List;

public class Neutrino_AbandonShipyard extends BaseSubmarketPlugin {

    private boolean playerPaidToUnlock = false;

    @Override
    public String getBuyVerb() {
        return "Retrieve";
    }

    @Override
    public DialogOption[] getDialogOptions(CoreUIAPI ui) {
        if (canPlayerAccess() && canPlayerAffordUnlock()) {
            return new DialogOption[]{
                new DialogOption("Access", new Script() {
                    @Override
                    public void run() {
                        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
                        playerFleet.getCargo().removeSupplies(getUnlockCost());
                        playerPaidToUnlock = true;
                        Global.getSector().addScript(new BuildUnsung(submarket));
                    }
                }),
                new DialogOption("Leave", null)
            };
        } else {
            return new DialogOption[]{
                new DialogOption("Leave", null)
            };
        }
    }

    @Override
    public String getDialogText(CoreUIAPI ui) {
        if (canPlayerAccess()) {
            if (canPlayerAffordUnlock()) {
                return "After a quick scan you find the abandoned Neutrino autonomous shipyard is still functional. You connect it using Neutrino secure-comms and it reports a unique one-run UAC is already installed and that a ship is already under construction. The progress is 98% but requires " + getUnlockCost() + " more supplies and " + getBuildTime() + " days to finish.";
            } else {
                return "After a quick scan you find the abandoned Neutrino autonomous shipyard is still functional. You connect it using Neutrino secure-comms and it reports a unique one-run UAC is already installed and that a ship is already under construction. The progress is 98% but requires " + getUnlockCost() + " more supplies and " + getBuildTime() + " days to finish. But you don't have enough supplies yet.";
            }
        }
        return "After a quick scan you find the abandoned Neutrino autonomous shipyard is still functional and a large unidentified ship is nearing completion. The station is requesting a Neutrino secure-comm to connect with of which you do not possess.";
    }

    @Override
    public Highlights getDialogTextHighlights(CoreUIAPI ui) {
        Highlights h = new Highlights();
        h.setText("" + getUnlockCost());
        if (canPlayerAffordUnlock()) {
            h.setColors(Misc.getHighlightColor());
        } else {
            h.setColors(Misc.getNegativeHighlightColor());
        }
        return h;
    }

    @Override
    public String getIllegalTransferText(CargoStackAPI stack, TransferAction action) {
        return "";
    }

    @Override
    public OnClickAction getOnClickAction(CoreUIAPI ui) {
        if (playerPaidToUnlock) {
            return OnClickAction.OPEN_SUBMARKET;
        } else if (checkData()) {
            playerPaidToUnlock = true;
            return OnClickAction.OPEN_SUBMARKET;
        }
        return OnClickAction.SHOW_TEXT_DIALOG;
    }

    @Override
    public String getSellVerb() {
        return "Leave";
    }

    @Override
    public float getTariff() {
        return 0f;
    }

    @Override
    public String getTooltipAppendix(CoreUIAPI ui) {
        return null;
    }

    @Override
    public Highlights getTooltipAppendixHighlights(CoreUIAPI ui) {
        return null;
    }

    @Override
    public void init(SubmarketAPI submarket) {
        super.init(submarket);
        Global.getSector().addScript(new CheckUnsung(submarket));
    }

    @Override
    public boolean isEnabled(CoreUIAPI ui) {
        return true;
    }

    @Override
    public boolean isFreeTransfer() {
        return true;

    }

    public void setPlayerPaidToUnlock(boolean playerPaidToUnlock) {
        this.playerPaidToUnlock = playerPaidToUnlock;
    }

    @Override
    public boolean isIllegalOnSubmarket(CargoStackAPI stack, TransferAction action) {
        return action == TransferAction.PLAYER_SELL;
    }

    @Override
    public boolean isIllegalOnSubmarket(String commodityId, TransferAction action) {
        return action == TransferAction.PLAYER_SELL;
    }

    @Override
    public boolean isIllegalOnSubmarket(FleetMemberAPI member, TransferAction action) {
        return action == TransferAction.PLAYER_SELL;
    }

    @Override
    public boolean isParticipatesInEconomy() {
        return false;
    }

    @Override
    public void updateCargoPrePlayerInteraction() {
    }

    private boolean canPlayerAffordUnlock() {
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        int supplies = (int) playerFleet.getCargo().getSupplies();
        return supplies >= getUnlockCost();
    }

    private boolean canPlayerAccess() {
        return Global.getSector().getFaction("neutrinocorp").getRelationshipLevel(Factions.PLAYER) == RepLevel.COOPERATIVE;
    }

    private int getUnlockCost() {
        return 4000;
    }

    private int getBuildTime() {
        return 30;
    }

    private static class BuildUnsung implements EveryFrameScript {

        protected float buildTimer = 0f;
        SubmarketAPI submarket;

        BuildUnsung(SubmarketAPI submarket) {
            buildTimer = 30f;
            this.submarket = submarket;
        }

        @Override
        public void advance(float amount) {
            float days = Global.getSector().getClock().convertToDays(amount);
            buildTimer -= days;
            if (buildTimer <= 0) {
                buildTimer = 0;
                submarket.getCargo().addMothballedShip(FleetMemberType.SHIP, "neutrino_unsung_Hull", null);
//                submarket.getCargo().addWeapons("neutrino_phasedarray", 2);
                setData(true);
                Global.getSector().getCampaignUI().addMessage("You got a fading signal through Neutrino secure-comm that inform you your build is complete.");
            }
        }

        @Override
        public boolean isDone() {
            return checkData();
        }

        @Override
        public boolean runWhilePaused() {
            return false;
        }
    }

    private static class CheckUnsung implements EveryFrameScript {

        IntervalUtil interval = new IntervalUtil(1f, 1f);
        SubmarketAPI submarket;

        CheckUnsung(SubmarketAPI submarket) {
            this.submarket = submarket;
        }

        @Override
        public void advance(float amount) {
            if(Global.getCurrentState().equals(Global.getCurrentState().COMBAT)){
                return;
            }
            interval.advance(amount);
            if (interval.intervalElapsed()) {
                CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
                List<FleetMemberAPI> members = playerFleet.getFleetData().getMembersListCopy();
                for (FleetMemberAPI member : members) {
                    if (member.getHullId().contentEquals("neutrino_unsung")) {
                        setData(true);
                    }
                }
            }
        }

        @Override
        public boolean isDone() {
            return checkData();
        }

        @Override
        public boolean runWhilePaused() {
            return true;
        }
    }

    private static boolean checkData() {
        Object Unsung_builded = Global.getSector().getPersistentData().get("Unsung_builded");
        if (Unsung_builded == null) {
            Global.getSector().getPersistentData().put("Unsung_builded", false);
            return false;
        }
        return ((boolean) Unsung_builded);
    }

    private static void setData(boolean builded) {
        Global.getSector().getPersistentData().put("Unsung_builded", builded);
    }
}
