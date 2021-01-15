package eu.opertusmundi.admin.web.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.opertusmundi.admin.web.model.dto.DashboardDto;

@Repository()
@Transactional(readOnly = true)
public class DashboardRepository {

	@PersistenceContext(unitName = "default")
	private EntityManager entityManager;

	public DashboardDto getDashboard(Integer id) {
		return new DashboardDto();
	}
}
