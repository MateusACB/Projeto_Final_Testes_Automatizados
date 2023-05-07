package br.com.ada.testeautomatizado.util;
import br.com.ada.testeautomatizado.dto.VeiculoDTO;
import br.com.ada.testeautomatizado.model.Veiculo;
import org.springframework.stereotype.Component;

@Component
public class VeiculoDTOConverter {

    public VeiculoDTO convertFrom(Veiculo veiculo) {
        return VeiculoDTO.builder()
                .placa(veiculo.getPlaca())
                .modelo(veiculo.getModelo())
                .marca(veiculo.getMarca())
                .disponivel(veiculo.getDisponivel())
                .dataFabricacao(veiculo.getDataFabricacao())
                .build();
    }

}
