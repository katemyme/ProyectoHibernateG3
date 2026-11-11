package ni.edu.uam.proyectohibernateg3.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import ni.edu.uam.proyectohibernateg3.database.JPAUtil;
import ni.edu.uam.proyectohibernateg3.model.Categoria;
import ni.edu.uam.proyectohibernateg3.model.Libro;

import java.util.List;

public class CategoriaDAO {

    public List<Categoria> listar() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("select c from Categoria  c", Categoria.class).getResultList();
        }finally {
            em.close();
        }
    }
}
