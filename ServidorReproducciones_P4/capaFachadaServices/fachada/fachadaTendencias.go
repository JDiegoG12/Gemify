package capafachada

import (
	entitys "tendencias/capaAccesoDatos/entities"
	capaaccesoadatos "tendencias/capaAccesoDatos/repositorios"
)

type FachadaTendencias struct {
	repo *capaaccesoadatos.RepositorioReproducciones
}

// Constructor de la fachada
func NuevaFachadaTendencias() *FachadaTendencias {
	return &FachadaTendencias{
		repo: capaaccesoadatos.GetRepositorio(),
	}
}
func (f *FachadaTendencias) RegistrarReproduccion(titulo string, cliente string) {
	f.repo.AgregarReproduccion(titulo, cliente)
}

func (f *FachadaTendencias) ObtenerReproducciones() []entitys.ReproduccionEntity {
	return f.repo.ListarReproducciones()
}
