package br.com.finsavior.test.util;

import br.com.finsavior.grpc.tables.AiAdviceRequest;
import br.com.finsavior.grpc.tables.AiAdviceResponse;
import br.com.finsavior.grpc.tables.TableDataServiceGrpc;
import io.grpc.stub.StreamObserver;

import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;

public class GrpcServicesUtil {
    public static final TableDataServiceGrpc.TableDataServiceImplBase generateAiAdviceAndInsightsServiceImpl =
            mock(TableDataServiceGrpc.TableDataServiceImplBase.class, delegatesTo(
                    new TableDataServiceGrpc.TableDataServiceImplBase() {
                        @Override
                        public void generateAiAdviceAndInsights(AiAdviceRequest request, StreamObserver<AiAdviceResponse> responseObserver) {
                            responseObserver.onNext(buildAiAdviceResponse());
                            responseObserver.onCompleted();
                        }
                    }));


    private static AiAdviceResponse buildAiAdviceResponse() {
        return AiAdviceResponse.newBuilder()
                .setAnalysisId(1L)
                .build();
    }
}
