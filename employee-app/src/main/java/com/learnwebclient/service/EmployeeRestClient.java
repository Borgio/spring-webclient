package com.learnwebclient.service;

import com.learnwebclient.dto.Employee;
import com.learnwebclient.exception.ClientDataException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.learnwebclient.constants.EmployeeConstants.*;

@Slf4j
public class EmployeeRestClient {

    private WebClient webClient;

    public EmployeeRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<RuntimeException> handle4xxErrorResponse(ClientResponse clientResponse) {
        Mono<String> errorResponse = clientResponse.bodyToMono(String.class);
        return errorResponse.flatMap((message) -> {
            log.error("ErrorResponse Code is " + clientResponse.rawStatusCode() + " and the exception message is : " + message);
            throw new ClientDataException(message);
        });
    }

    public List<Employee> retrieveAllEmployees() {
        try {
            return webClient.get().uri(GET_ALL_EMPLOYEES_V1)
                    .retrieve()
                    .bodyToFlux(Employee.class)
                    .collectList()
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("Error Response code is : {} and the message is {}", ex.getRawStatusCode(), ex.getResponseBodyAsString());
            log.error("WebClientResponseException in retrieveAllEmployees", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Exception in retrieveAllEmployees ", ex);
            throw ex;
        }
    }


    public Employee retrieveEmployeeById(int employeeId) {

        try {
            return webClient.get().uri(EMPLOYEE_BY_ID_V1, employeeId)
                    .retrieve()
                    .bodyToMono(Employee.class)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("Error Response code is : {} and the message is {}", ex.getRawStatusCode(), ex.getResponseBodyAsString());
            log.error("WebClientResponseException in retrieveEmployeeById", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Exception in retrieveEmployeeById ", ex);
            throw ex;
        }
    }

    public Employee retrieveEmployeeById_Custom_Error_Handling(int employeeId) {

        return webClient.get().uri(EMPLOYEE_BY_ID_V1, employeeId)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> handle4xxErrorResponse(clientResponse))
                .bodyToMono(Employee.class)
                .block();
    }


    public List<Employee> retrieveEmployeeByName(String employeeName) {

        String uri = UriComponentsBuilder.fromUriString(GET_EMPLOYEE_BY_NAME_V1)
                .queryParam("employee_name", employeeName)
                .build().toUriString();
        try {
            return webClient.get().uri(uri)
                    .retrieve()
                    .bodyToFlux(Employee.class)
                    .collectList()
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("Error Response code is : {} and the message is {}", ex.getRawStatusCode(), ex.getResponseBodyAsString());
            log.error("WebClientResponseException in retrieveEmployeeByName", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Exception in retrieveEmployeeByName ", ex);
            throw ex;
        }
    }

    public Employee addNewEmployee(Employee employee) {
        try {
            return webClient.post().uri(ADD_EMPLOYEE_V1)
                    .syncBody(employee)
                    .retrieve()
                    .bodyToMono(Employee.class)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("Error Response code is : {} and the message is {}", ex.getRawStatusCode(), ex.getResponseBodyAsString());
            log.error("WebClientResponseException in addNewEmployee", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Exception in addNewEmployee ", ex);
            throw ex;
        }
    }

    public Employee addNewEmployee_custom_Error_Handling(Employee employee) {
            return webClient.post().uri(ADD_EMPLOYEE_V1)
                    .syncBody(employee)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> handle4xxErrorResponse(clientResponse))
                    .bodyToMono(Employee.class)
                    .block();
    }

    public Employee updateEmployee(int id, Employee employee) {

        try {
            return webClient.put().uri(EMPLOYEE_BY_ID_V1, id)
                    .syncBody(employee)
                    .retrieve()
                    .bodyToMono(Employee.class)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("Error Response code is : {} and the message is {}", ex.getRawStatusCode(), ex.getResponseBodyAsString());
            log.error("WebClientResponseException in updateEmployee", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Exception in updateEmployee ", ex);
            throw ex;
        }
    }

    public String deleteEmployeeById(int id) {
        try {
            return webClient.delete().uri(EMPLOYEE_BY_ID_V1, id)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("Error Response code is : {} and the message is {}", ex.getRawStatusCode(), ex.getResponseBodyAsString());
            log.error("WebClientResponseException in updateEmployee", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Exception in updateEmployee ", ex);
            throw ex;
        }

    }
}
