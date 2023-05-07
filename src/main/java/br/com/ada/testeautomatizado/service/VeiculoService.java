package br.com.ada.testeautomatizado.service;

import br.com.ada.testeautomatizado.dto.VeiculoDTO;
import br.com.ada.testeautomatizado.exception.PlacaInvalidaException;
import br.com.ada.testeautomatizado.exception.VeiculoNaoEncontradoException;
import br.com.ada.testeautomatizado.model.Veiculo;
import br.com.ada.testeautomatizado.repository.VeiculoRepository;
import br.com.ada.testeautomatizado.dto.ResponseDTO;
import br.com.ada.testeautomatizado.util.ValidacaoPlaca;
import br.com.ada.testeautomatizado.util.VeiculoDTOConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private ValidacaoPlaca validacaoPlaca;

    @Autowired
    private VeiculoDTOConverter veiculoDTOConverter;

    public ResponseEntity<ResponseDTO<VeiculoDTO>> cadastrar(VeiculoDTO veiculoDTO) {
        try {
            this.validacaoPlaca.isPlacaValida(veiculoDTO.getPlaca());
            Veiculo veiculo = new Veiculo();
            veiculo.setPlaca(veiculoDTO.getPlaca());
            veiculo.setModelo(veiculoDTO.getModelo());
            veiculo.setMarca(veiculoDTO.getMarca());
            veiculo.setDisponivel(veiculoDTO.getDisponivel());
            veiculo.setDataFabricacao(veiculoDTO.getDataFabricacao());
            this.veiculoRepository.save(veiculo);
            return ResponseEntity.ok(new ResponseDTO<>("Sucesso", veiculoDTO));
        } catch (PlacaInvalidaException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new ResponseDTO<>(e.getMessage(), null));
        } catch (Exception e) {
            throw e;
        }
    }

    public ResponseEntity<ResponseDTO<Boolean>> deletarVeiculoPelaPlaca(String placa) {
        try {
            buscarVeiculoPelaPlaca(placa).ifPresent(this.veiculoRepository::delete);
            return ResponseEntity.ok(new ResponseDTO<>("Sucesso", Boolean.TRUE));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new ResponseDTO<>("", null));
        }
    }

    public ResponseEntity<ResponseDTO<VeiculoDTO>> atualizar(VeiculoDTO veiculoDTO) {

        try {

            this.validacaoPlaca.isPlacaValida(veiculoDTO.getPlaca());

            Optional<Veiculo> optionalVeiculo = this.veiculoRepository.findByPlaca(veiculoDTO.getPlaca());
            if (optionalVeiculo.isPresent()) {

                Veiculo veiculo = new Veiculo();
                veiculo.setId(optionalVeiculo.get().getId());
                veiculo.setPlaca(veiculoDTO.getPlaca());
                veiculo.setModelo(veiculoDTO.getModelo());
                veiculo.setMarca(veiculoDTO.getMarca());
                veiculo.setDisponivel(veiculoDTO.getDisponivel());
                veiculo.setDataFabricacao(veiculoDTO.getDataFabricacao());

                Veiculo veiculoAtualizadoBD = this.veiculoRepository.save(veiculo);

                VeiculoDTO veiculoDTOAtualizado = this.veiculoDTOConverter.convertFrom(veiculoAtualizadoBD);

                return ResponseEntity.ok(new ResponseDTO<>("Sucesso", veiculoDTOAtualizado));

            } else {
                throw new VeiculoNaoEncontradoException();
            }

        } catch (VeiculoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }

    public ResponseEntity<ResponseDTO<List<VeiculoDTO>>> listarTodos() {

        List<Veiculo> veiculos = veiculoRepository.findAll();

        List<VeiculoDTO> veiculoDTOS = new ArrayList<>();

        for (Veiculo veiculo : veiculos) {
            VeiculoDTO veiculoDTO = this.veiculoDTOConverter.convertFrom(veiculo);
            veiculoDTOS.add(veiculoDTO);
        }

        return ResponseEntity.ok(new ResponseDTO<>("Sucesso", veiculoDTOS));

    }

    private Optional<Veiculo> buscarVeiculoPelaPlaca(String placa) {
        return this.veiculoRepository.findByPlaca(placa);
    }
}

