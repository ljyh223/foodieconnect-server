package com.ljyh.foodieconnect.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljyh.foodieconnect.dto.ApiResponse;
import com.ljyh.foodieconnect.entity.ChatRoom;
import com.ljyh.foodieconnect.entity.Merchant;
import com.ljyh.foodieconnect.enums.ChatSessionStatus;
import com.ljyh.foodieconnect.service.ChatRoomService;
import com.ljyh.foodieconnect.service.MerchantAuthService;
import com.ljyh.foodieconnect.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MerchantChatRoomControllerTest {

    @Mock
    private ChatRoomService chatRoomService;

    @Mock
    private MerchantAuthService merchantAuthService;

    @Mock
    private RestaurantService restaurantService;

    @InjectMocks
    private MerchantChatRoomController merchantChatRoomController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(merchantChatRoomController).build();
        objectMapper = new ObjectMapper();
    }


}
