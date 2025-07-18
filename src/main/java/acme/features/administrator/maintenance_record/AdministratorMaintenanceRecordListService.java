
package acme.features.administrator.maintenance_record;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student5.MaintenanceRecord;

@GuiService
public class AdministratorMaintenanceRecordListService extends AbstractGuiService<Administrator, MaintenanceRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorMaintenanceRecordRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<MaintenanceRecord> maintenanceRecords;

		maintenanceRecords = this.repository.findPublishedMaintenanceRecords();

		super.getBuffer().addData(maintenanceRecords);

	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		Dataset dataset;

		dataset = super.unbindObject(maintenanceRecord, "moment", "status", "nextInspectionDueTime");
		super.addPayload(dataset, maintenanceRecord, //
			"estimatedCost", "notes", "draftMode", "aircraft.model", //
			"aircraft.registrationNumber", "technician.identity.fullName",//
			"technician.licenseNumber", "technician.phoneNumber");

		super.getResponse().addData(dataset);
	}
}
