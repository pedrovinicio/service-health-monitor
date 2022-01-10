import React from "react";
import axios from "axios";
import { render, screen, act } from "@testing-library/react";
import App from "./App";

jest.mock("axios");

describe("Overview", () => {
  
    it("Should show error message when service is down", async () => {
        const promise = Promise.resolve();

        // Mock
        axios.get.mockImplementation(() =>
            Promise.reject(new Error("Network Error"))
        );

        render(<App />);

        const errorText = await screen.findByText("Something is wrong with our server :(");
        expect(errorText).toBeDefined();

        await act(async () => await promise);
    });

    it("Should NOT show service list is when receives an EMPTY list from the server", async () => {
        const promise = Promise.resolve();

        // Mock
        const mockedServiceList = {data: { services: [] }};
        axios.get.mockResolvedValue(mockedServiceList);

        render(<App />);

        const serviceList = screen.findByTestId("service-list");
        expect(JSON.stringify(serviceList)).toEqual(JSON.stringify({}));

        await act(async () => await promise);
    });

    it("Should show service list is when receives NON-EMPTY list from the server", async () => {
        const promise = Promise.resolve();

        // Mock
        const mockedServiceList = {data: { services: [
            {
                Id: 1,
                Name: "Google",
                Url: "https://www.google.com",
                Status: 1,
                Created: "2022-01-08T10:22:11",
                LastVerified: "2022-01-08T10:22:11"
              },
              {
                Id: 2,
                Name: "UOL",
                Url: "https://www.uol.fr",
                Status: 1,
                Created: "2022-01-08T10:22:11",
                LastVerified: "2022-01-08T10:22:11"
              },
        ] }};
        axios.get.mockResolvedValue(mockedServiceList);

        render(<App />);

        const service1Name = await screen.findByText("Google");
        const service2Name = await screen.findByText("UOL");
        expect(service1Name).toBeDefined();
        expect(service2Name).toBeDefined();

        await act(async () => await promise);
    });

});
