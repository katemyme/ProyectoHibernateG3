package ni.edu.uam.proyectohibernateg3.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import ni.edu.uam.proyectohibernateg3.database.JPAUtil;
import ni.edu.uam.proyectohibernateg3.model.Categoria;
import ni.edu.uam.proyectohibernateg3.model.Libro;

import java.util.List;

public class LibroDAO {

    private Categoria obtenerCategoria(EntityManager em, Libro libro)
    {
        if(libro.getCategoria()==null || libro.getCategoria().getId() == null)
        {
            throw new RuntimeException("Error al obtener categoria");
        }
        Long categoriaId = libro.getCategoria().getId();
        Categoria categoria = em.find(Categoria.class, categoriaId);
        if(categoria == null)
        {
            throw new RuntimeException("Error al obtener categoria");
        }
        return categoria;
    }


    public void guardar(Libro libro) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try{
            // Iniciar la transacción en PostgreSQL
            tx.begin();
            Categoria categoria = obtenerCategoria(em, libro);
            libro.setCategoria(categoria);
            // Decirle a Hibernate que prepare la persistencia del objeto.
            em.persist(libro);
            // Confirmar los cambios en la base de datos
            tx.commit();
        }catch(RuntimeException ex){
            if(tx.isActive()){
                tx.rollback();
            }
            throw ex;
        }finally {
            em.close();
        }
    }

    public void actualizar(Libro libro) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            // merge copia los datos del objeto sobre el registro existente con el mismo id
            Categoria categoria = obtenerCategoria(em, libro);
            libro.setCategoria(categoria);
            em.merge(libro);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    // Devuelve true si el libro existía y se eliminó, false si no se encontró
    public boolean eliminar(long id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Libro libro = em.find(Libro.class, id);
            if (libro == null) {
                tx.rollback();
                return false;
            }
            em.remove(libro);
            tx.commit();
            return true;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public List<Libro> listar() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT l FROM Libro l JOIN FETCH l.categoria ORDER BY l.id", Libro.class).getResultList();
        }
        finally {
            em.close();
        }
    }
}