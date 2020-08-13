package git.stacyamdev.medicalapp.model.database.dao;

import git.stacyamdev.medicalapp.model.entity.Identified;
import git.stacyamdev.medicalapp.model.exception.MedicalException;

import java.util.List;

public interface DaoInterface<E extends Identified> {

    /**
     * Создает новую запись, соответствующую объекту entity
     */
    E persist(E entity) throws MedicalException;

    E getByKey(Long key) throws MedicalException;

    void update(E entity) throws MedicalException;

    void delete(E entity) throws MedicalException;

    List<E> getAll() throws MedicalException;
}
