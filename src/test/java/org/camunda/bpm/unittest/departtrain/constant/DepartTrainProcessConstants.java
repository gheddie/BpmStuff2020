package org.camunda.bpm.unittest.departtrain.constant;

public class DepartTrainProcessConstants {

	// ##############################################################################
	// ####################################### tasks
	// ##############################################################################
	
	public static final String TASK_CHOOSE_EXIT_TRACK = "TASK_CHOOSE_EXIT_TRACK";
	public static final String TASK_CHECK_WAGGONS = "TASK_CHECK_WAGGONS";
	public static final String TASK_CONFIRM_ROLLOUT = "TASK_CONFIRM_ROLLOUT";
	public static final String TASK_PROCESS_REPAIR = "TASK_PROCESS_REPAIR";
	public static final String TASK_ASSUME_REPAIR_TIME = "TASK_ASSUME_REPAIR_TIME";

	// ##############################################################################
	// ####################################### signals
	// ##############################################################################
	
	public static final String SIG_RO_CANC = "SIG_RO_CANC";

	// ##############################################################################
	// ####################################### variables
	// ##############################################################################
	
	public static final String VAR_POSITIONS_OK = "positionsOk";
	
	public static final String VAR_EXIT_TRACK = "exitTrack";
	
	// Gesamtliste, die in den Prozess eingegeben wird
	public static final String VAR_PLANNED_WAGGONS = "VAR_PLANNED_WAGGONS";
	
	// Entscheidet, ob zu 'TaskChooseExitTrack' übergegangen wird ---> 'TaskAllRepairsDone'
	public static final String VAR_ALL_REPAIRS_DONE = "VAR_ALL_REPAIRS_DONE";
	
	// Hier werden in 'TaskAllRepairsDone' alle zurückgemeldeten Reparaturen gespeichert
	// Es wird zu 'TaskChooseExitTrack' weitergegeben, wenn gilt: ('VAR_REPAIRED_WAGGONS' == 'VAR_WAGGONS_TO_REPAIR')
	public static final String VAR_REPAIRED_WAGGONS = "VAR_REPAIRED_WAGGONS";
	
	// Hier wird sich ab 'TaskProcessCriticalErrors' gemerkt, welche Wagen als repariert zurückzumelden sind
	public static final String VAR_WAGGONS_TO_REPAIR = "VAR_WAGGONS_TO_REPAIR";
	
	// Wird durch den Reparatur-Prozess geschleift und auuch von diesem zurückgegeben
	public static final String VAR_SINGLE_WAGGON_TO_REPAIR = "VAR_SINGLE_WAGGON_TO_REPAIR";
	
	// business key of the 'master' process --> passed to repair 
	// process to able to call back to master
	public static final String VAR_DEP_PROC_BK = "VAR_DEP_PROC_BK";

	// ##############################################################################
	// ####################################### messages
	// ##############################################################################
	
	public static final String MSG_INVOKE_WAG_REP = "MSG_INVOKE_WAG_REP";
	public static final String MSG_DEPARTURE_PLANNED = "MSG_DEPARTURE_PLANNED";
	public static final String MSG_WG_REPAIRED = "MSG_WG_REPAIRED";
	
	// ##############################################################################
	// ####################################### errors
	// ##############################################################################
	
	public static final String ERR_NO_EXIT_TR = "ERR_NO_EXIT_TR";
	
	// ##############################################################################
	// ####################################### message catchers
	// ##############################################################################
	
	public static final String CATCH_MSG_WG_REPAIRED = "CATCH_MSG_WG_REPAIRED";
	
	// ##############################################################################
	// ####################################### processes
	// ##############################################################################
	
	public static final String PROCESS_REPAIR_FACILITY = "PROCESS_REPAIR_FACILITY";
}