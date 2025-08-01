
package acme.features.administrator.passenger;

import java.util.Collection;
import java.util.Date;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student2.Passenger;

@GuiService
public class AdministratorBannedPassengersListService extends AbstractGuiService<Administrator, Passenger> {

	// Internal state ---------------------------------------------------------

	private final AdministratorPassengerRepository repository;

	// Constructor ------------------------------------------------------------


	public AdministratorBannedPassengersListService(final AdministratorPassengerRepository repository) {
		this.repository = repository;
	}

	// AbstractGuiService interface -------------------------------------------

	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Administrator.class));
	}

	@Override
	public void load() {
		Collection<Passenger> passengers;

		Date today = MomentHelper.getCurrentMoment();
		passengers = this.repository.findBannedPassengers(today);

		super.getBuffer().addData(passengers);
	}

	@Override
	public void unbind(final Passenger passengers) {
		Dataset dataset;
		String specialNeeds = passengers.getSpecialNeeds();
		String nationality = this.repository.findNationality(passengers.getId()).get(0);

		dataset = super.unbindObject(passengers, "fullName", "email", "passportNumber", "dateOfBirth");
		dataset.put("specialNeeds", specialNeeds.isBlank() ? "N/A" : specialNeeds);
		dataset.put("nationality", nationality);

		super.getResponse().addData(dataset);
		super.getResponse().addGlobal("banned", true);
	}

}
