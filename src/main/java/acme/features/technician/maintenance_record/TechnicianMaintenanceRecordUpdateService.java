
package acme.features.technician.maintenance_record;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.group.Aircraft;
import acme.entities.student5.MaintenanceRecord;
import acme.entities.student5.MaintenanceRecordStatus;
import acme.realms.Technician;

@GuiService
public class TechnicianMaintenanceRecordUpdateService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		boolean status = false;
		boolean statusAircraft = true;
		int maintenanceRecordId;
		MaintenanceRecord maintenanceRecord;
		boolean isDraft;
		boolean isTechnician;
		int aircraftId;
		Aircraft aircraft;

		if (super.getRequest().hasData("id", int.class)) {
			maintenanceRecordId = super.getRequest().getData("id", int.class);
			maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);

			if (maintenanceRecord != null) {
				Technician technician = maintenanceRecord.getTechnician();
				isDraft = maintenanceRecord.isDraftMode();
				isTechnician = super.getRequest().getPrincipal().hasRealm(technician);

				status = isDraft && isTechnician;
			}
		}

		if (super.getRequest().hasData("aircraft", int.class)) {
			aircraftId = super.getRequest().getData("aircraft", int.class);
			aircraft = this.repository.findAircraftById(aircraftId);

			if (aircraft == null && aircraftId != 0)
				statusAircraft = false;
		}

		super.getResponse().setAuthorised(status && statusAircraft);

	}

	@Override
	public void load() {
		MaintenanceRecord maintenanceRecord;
		int maintenanceRecordId;

		maintenanceRecordId = super.getRequest().getData("id", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);

		super.getBuffer().addData(maintenanceRecord);
	}

	@Override
	public void bind(final MaintenanceRecord maintenanceRecord) {
		int aircraftId;
		Aircraft aircraft;

		aircraftId = super.getRequest().getData("aircraft", int.class);
		aircraft = this.repository.findAircraftById(aircraftId);

		super.bindObject(maintenanceRecord, "moment", "status", "nextInspectionDueTime", "estimatedCost", "notes");
		maintenanceRecord.setAircraft(aircraft);
	}

	@Override
	public void validate(final MaintenanceRecord maintenanceRecord) {
		;
	}

	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {
		this.repository.save(maintenanceRecord);
	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		Collection<Aircraft> aircrafts;
		SelectChoices choicesAircrafts;
		SelectChoices choicesStatus;
		Dataset dataset;

		aircrafts = this.repository.findAllAircrafts();

		choicesStatus = SelectChoices.from(MaintenanceRecordStatus.class, maintenanceRecord.getStatus());
		choicesAircrafts = SelectChoices.from(aircrafts, "registrationNumber", maintenanceRecord.getAircraft());

		dataset = super.unbindObject(maintenanceRecord, "moment", "nextInspectionDueTime", "estimatedCost", "notes", "draftMode");
		dataset.put("technician", maintenanceRecord.getTechnician().getIdentity().getFullName());
		dataset.put("aircraft", choicesAircrafts.getSelected().getKey());
		dataset.put("aircrafts", choicesAircrafts);
		dataset.put("status", choicesStatus.getSelected().getKey());
		dataset.put("statuses", choicesStatus);

		super.getResponse().addData(dataset);
	}

}
