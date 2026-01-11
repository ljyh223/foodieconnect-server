package com.ljyh.tabletalk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljyh.tabletalk.dto.ApiResponse;
import com.ljyh.tabletalk.entity.ChatRoom;
import com.ljyh.tabletalk.entity.Merchant;
import com.ljyh.tabletalk.enums.ChatSessionStatus;
import com.ljyh.tabletalk.service.ChatRoomService;
import com.ljyh.tabletalk.service.MerchantAuthService;
import com.ljyh.tabletalk.service.RestaurantService;
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
