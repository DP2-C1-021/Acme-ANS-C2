
package acme.features.assistance_agent.tracking_log;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.student4.Claim;
import acme.entities.student4.Indicator;
import acme.entities.student4.TrackingLog;

@Repository
public interface AssistanceAgentTrackingLogRepository extends AbstractRepository {

	@Query("select t from TrackingLog t where t.claim.id = :id")
	public Collection<TrackingLog> findManyTrackingLogsClaimId(int id);

	@Query("select t from TrackingLog t where t.claim.id = :id and t.indicator != :indicator and t.draftMode = false")
	public Collection<TrackingLog> findManyTrackingLogsClaimIdAndIndicator(int id, Indicator indicator);

	@Query("select c from Claim c where c.id = :id")
	public Claim findOneClaimById(int id);

	@Query("select t from TrackingLog t where t.id = :id")
	public TrackingLog findOneTrackingLogById(int id);

	@Query("select max(t.resolutionPercentage) from TrackingLog t where t.claim.id = :claimId and t.id != :id and t.draftMode = false")
	public Double findMaxResolutionPercentageByClaimId(int id, int claimId);

	@Query("SELECT COUNT(t) FROM TrackingLog t WHERE t.claim.id = :claimId AND t.resolutionPercentage = 100.00 AND t.draftMode = false")
	public Long countTrackingLogsForExceptionalCase(int claimId);

}
