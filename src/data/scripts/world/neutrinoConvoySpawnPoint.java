//package data.scripts.world;
//
//import java.util.List;
//
//import com.fs.starfarer.api.Script;
//import com.fs.starfarer.api.campaign.CampaignFleetAPI;
//import com.fs.starfarer.api.campaign.CargoAPI;
//import com.fs.starfarer.api.campaign.FleetAssignment;
//import com.fs.starfarer.api.campaign.LocationAPI;
//import com.fs.starfarer.api.campaign.SectorAPI;
//import com.fs.starfarer.api.campaign.SectorEntityToken;
//import com.fs.starfarer.api.fleet.FleetMemberType;
//import com.fs.starfarer.api.campaign.CargoAPI.CrewXPLevel;
//
//@SuppressWarnings("unchecked")
//public class neutrinoConvoySpawnPoint extends BaseSpawnPoint {
//
//	private final SectorEntityToken convoyDestination;
//
//	public neutrinoConvoySpawnPoint(SectorAPI sector, LocationAPI location, 
//							float daysInterval, int maxFleets, SectorEntityToken anchor,
//							SectorEntityToken convoyDestination) {
//		super(sector, location, daysInterval, maxFleets, anchor);
//		this.convoyDestination = convoyDestination;
//	}
//
//	private static int convoyNumber = 0;
//	
//	@Override
//	protected CampaignFleetAPI spawnFleet() {
//	
//		String type = null;
//		float r = (float) Math.random();
//		if (r > .9f) {
//			type = "freighterSmall";
//		} else {
//			type = "freighterLarge";
//		}	
//
//		CampaignFleetAPI fleet = getSector().createFleet("neutrinocorp", type);
//		getLocation().spawnFleet(getAnchor(), 0, 0, fleet);
//		
//		CargoAPI cargo = fleet.getCargo();
//		addRandomWeapons(cargo, 3);
//		cargo.addCrew(CrewXPLevel.ELITE, 10);
//		cargo.addCrew(CrewXPLevel.VETERAN, 40);
//		addRandomShips(fleet.getCargo(), (int) (Math.random() * 2f));
//		
//		Script script = null;
//		script = createArrivedScript();
//		//Global.getSectorAPI().addMessage("A supply fleet has arrived for the Neutrino Corp, enroute to their station.");
//				
//		fleet.addAssignment(FleetAssignment.DELIVER_RESOURCES, convoyDestination, 1000, script);
//		fleet.addAssignment(FleetAssignment.GO_TO_LOCATION_AND_DESPAWN, getAnchor(), 1000);
//		
//		return fleet;
//	}
//	
//	private Script createArrivedScript() {
//		return new Script() {
//			public void run() {
//				//Global.getSectorAPI().addMessage("A Neutrino Corp supply convoy has delivered new equipment to their station");
//			}
//		};
//	}
//	
//	private void addRandomWeapons(CargoAPI cargo, int count) {
//		List weaponIds = getSector().getAllWeaponIds();
//		for (int i = 0; i < count; i++) {
//      String weapon = (String) weapons[(int) (weapons.length * Math.random())];
//			int quantity = (int)(Math.random() * 4f + 2f);
//			cargo.addWeapons(weapon, quantity);
//			
//		}
//	}
//	
//	private void addRandomShips(CargoAPI cargo, int count) {
//		List weaponIds = getSector().getAllWeaponIds();
//		for (int i = 0; i < count; i++) {
//			if ((float) Math.random() > 0.6f) {
//				String wing = (String) wings[(int) (wings.length * Math.random())];
//				cargo.addMothballedShip(FleetMemberType.FIGHTER_WING, wing, null);
//			} else {
//				String ship = (String) ships[(int) (ships.length * Math.random())];
//				cargo.addMothballedShip(FleetMemberType.SHIP, ship, null);
//			}
//		}
//	}
//
//	private static String [] ships = { 
//									"neutrino_lathe_standard",
//									"neutrino_piledriver_standard",
//									"neutrino_relativity_standard",									
//									"neutrino_singularity_standard",
//									"neutrino_singularity_standard",	
//									"neutrino_causality_standard",						
//									"neutrino_nausicaa2_standard",
//									"neutrino_nirvash_standard",
//									"neutrino_grinder_standard",									
//									"neutrino_hacksaw_standard",
//									"neutrino_hacksaw_assault",									
//									"neutrino_jackhammer2_standard",
//									"neutrino_hildolfr_standard",
//									"neutrino_vice_standard",						
//									"neutrino_banshee_standard",	
//									"neutrino_colossus_standard",
//									"neutrino_adventure_standard",
//									"neutrino_theend_standard",
//									"neutrino_sledgehammer_standard",
//									"neutrino_polarity_standard",		
//									"neutrino_criticality_standard",									
//									};
//
//	private static String [] wings = { 
//									"neutrino_drohne_wing",
//									"neutrino_drohne_wing",
//									"neutrino_drohne_wing",
//									"neutrino_schwarzgeist_wing",
//									"neutrino_schwarm_wing",									
//									"neutrino_drache_wing",									
//									"neutrino_floh_wing",		
//									"neutrino_gepard1_wing",	
//									};
//	
//	private static String [] weapons = { 
//									"autopulse",
//									"atropos", 
//									"atropos_single", 
//									"harpoon", 
//									"harpoon_single",
//									"harpoonpod", 
//									"hurricane", 
//									"mjolnir",
//									"neutrino_photontorpedo",
//									"neutrino_sapper",									
//									"neutrino_lightphoton",
//									"neutrino_antiproton",	
//									"neutrino_tractorbeam",
//									"neutrino_pulsebeam",
//									"neutrino_javelin",		
//									"neutrino_particlecannonarray",		
//									"neutrino_darkmatterbeamcannon",	
//									"neutrino_advancedtorpedo",
//									"neutrino_advancedtorpedosingle",
//									"neutrino_XLadvancedtorpedo",	
//									"neutrino_pulsar",	
//									"neutrino_dualpulsar",	
//									"neutrino_heavypulsar",
//									"neutrino_dualpulsebeam",	
//									"neutrino_disruptor",	
//									"neutrino_derp_launcher",	
//									"neutrino_neutronpulse",
//									"neutrino_neutronpulsebattery",
//									"neutrino_neutronpulseheavy",		
//									"neutrino_bane",		
//									"neutrino_misery",			
//									"neutrino_goliath",										
//									"phasecl", 
//									"pilum",
//									"pdburst",
//									"sabot", 
//									"sabot_single",
//									"sabotpod", 
//									"salamanderpod", 
//									"swarmer", 
//									"taclaser", 
//									};
//									
//}
//
//
//
//
//
