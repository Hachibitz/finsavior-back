package br.com.finsavior.test.util;

import br.com.finsavior.grpc.tables.AiAdviceRequest;
import br.com.finsavior.grpc.tables.AiAdviceResponse;
import br.com.finsavior.grpc.tables.TableDataServiceGrpc;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

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
                }));
    }

    private static AiAdviceResponse buildAiAdviceResponse() {
        return AiAdviceResponse.newBuilder()
                .setAnalysisId(1L)
                .build();
    }
}
