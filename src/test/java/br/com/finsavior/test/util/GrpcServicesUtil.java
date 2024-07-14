package br.com.finsavior.test.util;

import br.com.finsavior.grpc.tables.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.time.LocalDateTime;

import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;

public class GrpcServicesUtil {

    public static TableDataServiceGrpc.TableDataServiceImplBase getGenerateAiAdviceAndInsightsServiceImplSuccessCall() {
        return mock(TableDataServiceGrpc.TableDataServiceImplBase.class, delegatesTo(
                new TableDataServiceGrpc.TableDataServiceImplBase() {
                    @Override
                    public void generateAiAdviceAndInsights(AiAdviceRequest request, StreamObserver<AiAdviceResponse> responseObserver) {
                        responseObserver.onNext(buildAiAdviceResponse());
                        responseObserver.onCompleted();
                    }

                    @Override
                    public void deleteAiAnalysis(DeleteAiAnalysisRequest request, StreamObserver<GenericResponse> responseObserver) {
                        responseObserver.onNext(buildGenericResponse());
                        responseObserver.onCompleted();
                    }

                    @Override
                    public void getAiAnalysisList(AiAnalysisRequest request, StreamObserver<AiAnalysisResponse> responseObserver) {
                        responseObserver.onNext(buildAiAnalysisResponse());
                        responseObserver.onCompleted();
                    }
                }));
    }

    public static TableDataServiceGrpc.TableDataServiceImplBase getGenerateAiAdviceAndInsightsServiceImplFailureCall() {
        return mock(TableDataServiceGrpc.TableDataServiceImplBase.class, delegatesTo(
                new TableDataServiceGrpc.TableDataServiceImplBase() {
                    @Override
                    public void generateAiAdviceAndInsights(AiAdviceRequest request, StreamObserver<AiAdviceResponse> responseObserver) {
                        Status status = Status.INTERNAL.withDescription("Não é possível gerar informação de IA: Falha na comunicação com a API de inteligência artificial");
                        responseObserver.onError(status.asRuntimeException());
                    }

                    @Override
                    public void deleteAiAnalysis(DeleteAiAnalysisRequest request, StreamObserver<GenericResponse> responseObserver) {
                        Status status = Status.INTERNAL.withDescription("Falha ao deletar dados do usuário");
                        responseObserver.onError(status.asRuntimeException());
                    }

                    @Override
                    public void getAiAnalysisList(AiAnalysisRequest request, StreamObserver<AiAnalysisResponse> responseObserver) {
                        Status status = Status.INTERNAL.withDescription("Não é possível carregar informação de IA - Falha na comunicação com o servidor");
                        responseObserver.onError(status.asRuntimeException());
                    }
                }));
    }

    private static AiAdviceResponse buildAiAdviceResponse() {
        return AiAdviceResponse.newBuilder()
                .setAnalysisId(1L)
                .build();
    }

    private static GenericResponse buildGenericResponse() {
        return GenericResponse.newBuilder()
                .setMessage("message")
                .build();
    }

    private static AiAnalysisResponse buildAiAnalysisResponse() {
        AiAnalysis analysis = AiAnalysis.newBuilder()
                .setId(1L)
                .build();

        AiAnalysisResponse.Builder responseBuilder = AiAnalysisResponse.newBuilder();
        responseBuilder.addAiAnalysisList(analysis);

        return responseBuilder.build();
    }
}
